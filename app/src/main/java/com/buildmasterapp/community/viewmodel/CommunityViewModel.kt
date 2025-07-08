package com.buildmasterapp.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.community.data.CommunityRepository
import com.buildmasterapp.community.data.model.Comment
import com.buildmasterapp.community.data.model.CreateCommentRequest
import com.buildmasterapp.community.data.model.CreatePostRequest
import com.buildmasterapp.community.data.model.PostItem
import com.buildmasterapp.user.data.UserTokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import kotlinx.coroutines.withContext

// por el momento el front es local falta implementar el usuario que es otra bc user
data class CommunityUiState(
    val posts: List<PostItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showNewPostDialog: Boolean = false,
    val commentingPostId: String? = null, // ID del post al que se está comentando
    val authorOfCommentingPost: String? = null // Para el título del diálogo
)

sealed class CommunityUserEvent {
    data class ShowSnackbar(val message: String) : CommunityUserEvent()
}

class CommunityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CommunityRepository

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<CommunityUserEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    private val _realTimeUpdates = MutableSharedFlow<PostItem>()
    val realTimeUpdates = _realTimeUpdates.asSharedFlow()

    private var webSocket: WebSocket? = null

    init {
        val tokenProvider = UserTokenProvider(application.applicationContext)
        repository = CommunityRepository(tokenProvider)
        loadPosts()
        connectToWebSocket()
    }

    // Cargar posts desde el backend
    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val response = repository.getPosts()
            if (response.isSuccessful) {
                val posts = response.body() ?: emptyList()
                // Mapeo de NetworkPost a PostItem para la UI
                val postItems = posts.map { networkPost ->
                    PostItem(
                        id = networkPost.id.toString(),
                        authorId = networkPost.user_id,
                        authorName = networkPost.username,
                        authorProfilePictureUrl = null, // TODO: Poblar con la URL del perfil
                        title = networkPost.title,
                        timestamp = System.currentTimeMillis(), // TODO: Usar timestamp de la API si existe
                        content = networkPost.content,
                        likes = networkPost.likes_count,
                        dislikes = networkPost.dislikes_count,
                        repostsCount = 0, // TODO: Mapear si la API lo provee
                        isLikedByCurrentUser = false, // TODO: Mapear si la API lo provee
                        isDislikedByCurrentUser = false, // TODO: Mapear si la API lo provee
                        comments = networkPost.comments.map { c ->
                            Comment(
                                id = c.id.toString(),
                                author = c.username, // Usar el username de la API para comentarios
                                text = c.content,
                                timestamp = System.currentTimeMillis() // TODO: Usar timestamp de la API
                            )
                        }.toMutableList(),
                        mediaUrls = networkPost.media_urls ?: emptyList() // <--- CORRECCIÓN AQUÍ
                    )
                }
                _uiState.update { it.copy(posts = postItems, isLoading = false, error = null) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar posts") }
            }
        }
    }

    // Publicar un nuevo post
    fun publishPost(title: String, content: String, mediaUrls: List<String> = emptyList()) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val request = CreatePostRequest(title, content, mediaUrls)
            val response = repository.createPost(request)
            if (response.isSuccessful) {
                loadPosts() // Recarga los posts
                _eventChannel.send(CommunityUserEvent.ShowSnackbar("¡Post publicado!"))
            } else {
                _uiState.update { it.copy(isLoading = false) }
                _eventChannel.send(CommunityUserEvent.ShowSnackbar("Error al publicar post"))
            }
        }
    }

    // Comentar un post
    fun commentOnPost(postId: Int, comment: String) {
        viewModelScope.launch {
            val request = CreateCommentRequest(comment)
            val response = repository.commentPost(postId, request)
            if (response.isSuccessful) {
                loadPosts()
                _eventChannel.send(CommunityUserEvent.ShowSnackbar("¡Comentario publicado!"))
            } else {
                _eventChannel.send(CommunityUserEvent.ShowSnackbar("Error al comentar"))
            }
        }
    }

    // Dar like a un post
    fun likePost(postId: Int) {
        viewModelScope.launch {
            // Actualización optimista de la UI
            val currentPosts = _uiState.value.posts
            val postIndex = currentPosts.indexOfFirst { it.id == postId.toString() }
            if (postIndex != -1) {
                val post = currentPosts[postIndex]
                val isAlreadyLiked = post.isLikedByCurrentUser
                val updatedPost = post.copy(
                    isLikedByCurrentUser = !isAlreadyLiked,
                    likes = if (isAlreadyLiked) post.likes - 1 else post.likes + 1
                )
                val updatedPosts = currentPosts.toMutableList().apply { set(postIndex, updatedPost) }
                _uiState.update { it.copy(posts = updatedPosts) }
            }

            try {
                val response = repository.likePost(postId)
                if (!response.isSuccessful) {
                    throw Exception("Error al dar like")
                }
            } catch (e: Exception) {
                // Revertir el cambio en la UI si falla
                _eventChannel.send(CommunityUserEvent.ShowSnackbar(e.message ?: "Error desconocido"))
                loadPosts() // Recargar para asegurar consistencia
            }
        }
    }

    // Dar dislike a un post
    fun dislikePost(postId: Int) {
        viewModelScope.launch {
            // Actualización optimista de la UI
            val currentPosts = _uiState.value.posts
            val postIndex = currentPosts.indexOfFirst { it.id == postId.toString() }
            if (postIndex != -1) {
                val post = currentPosts[postIndex]
                val isAlreadyDisliked = post.isDislikedByCurrentUser
                val updatedPost = post.copy(
                    isDislikedByCurrentUser = !isAlreadyDisliked,
                    dislikes = if (isAlreadyDisliked) post.dislikes - 1 else post.dislikes + 1
                )
                val updatedPosts = currentPosts.toMutableList().apply { set(postIndex, updatedPost) }
                _uiState.update { it.copy(posts = updatedPosts) }
            }

            try {
                val response = repository.dislikePost(postId)
                if (!response.isSuccessful) {
                    throw Exception("Error al dar dislike")
                }
            } catch (e: Exception) {
                // Revertir el cambio en la UI si falla
                _eventChannel.send(CommunityUserEvent.ShowSnackbar(e.message ?: "Error desconocido"))
                loadPosts() // Recargar para asegurar consistencia
            }
        }
    }

    // Repostear un post
    fun repost(postId: Int) {
        viewModelScope.launch {
            val response = repository.repost(postId)
            if (response.isSuccessful) {
                loadPosts()
                _eventChannel.send(CommunityUserEvent.ShowSnackbar("¡Reposteado!"))
            } else {
                _eventChannel.send(CommunityUserEvent.ShowSnackbar("Error al repostear"))
            }
        }
    }

    // Funciones para manejar el diálogo de comentario
    fun openCommentDialog(postId: String) {
        val post = _uiState.value.posts.find { it.id == postId }
        _uiState.update { it.copy(commentingPostId = postId, authorOfCommentingPost = post?.authorName) }
    }

    fun dismissCommentDialog() {
        _uiState.update { it.copy(commentingPostId = null, authorOfCommentingPost = null) }
    }

    // Funciones para manejar el diálogo de nuevo post
    fun openNewPostDialog() {
        _uiState.update { it.copy(showNewPostDialog = true) }
    }

    fun dismissNewPostDialog() {
        _uiState.update { it.copy(showNewPostDialog = false) }
    }

    private fun connectToWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("wss://your-backend-url.com/updates") // Cambia esto por la URL de tu backend
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch {
                    handleWebSocketMessage(text)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                viewModelScope.launch {
                    handleWebSocketMessage(bytes.utf8())
                }
            }
        })
    }

    private suspend fun handleWebSocketMessage(message: String) {
        withContext(Dispatchers.IO) {
            // Parsear el mensaje y actualizar el estado de la UI
            val updatedPost = parsePostFromMessage(message)
            _realTimeUpdates.emit(updatedPost)

            _uiState.update { currentState ->
                val updatedPosts = currentState.posts.map { post ->
                    if (post.id == updatedPost.id) updatedPost else post
                }
                currentState.copy(posts = updatedPosts)
            }
        }
    }

    private fun parsePostFromMessage(message: String): PostItem {
        // Parsear el JSON recibido del backend
        val json = org.json.JSONObject(message)
        val mediaUrls = mutableListOf<String>()
        val mediaArray = json.optJSONArray("media_urls")
        if (mediaArray != null) {
            for (i in 0 until mediaArray.length()) {
                mediaUrls.add(mediaArray.getString(i))
            }
        }
        val comments = mutableListOf<Comment>()
        val commentsArray = json.optJSONArray("comments")
        if (commentsArray != null) {
            for (i in 0 until commentsArray.length()) {
                val c = commentsArray.getJSONObject(i)
                comments.add(
                    Comment(
                        id = c.optInt("id").toString(),
                        author = c.optString("username"),
                        text = c.optString("content")
                    )
                )
            }
        }
        return PostItem(
            id = json.optInt("id").toString(),
            authorId = json.optInt("user_id"),
            authorName = json.optString("username"),
            title = json.optString("title"),
            content = json.optString("content"),
            likes = json.optInt("likes_count"),
            dislikes = json.optInt("dislikes_count"),
            comments = comments,
            mediaUrls = (json.optJSONArray("media_urls")?.let { arr ->
                List(arr.length()) { arr.optString(it) }
            } ?: emptyList())
        )
    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.close(1000, null)
    }
}