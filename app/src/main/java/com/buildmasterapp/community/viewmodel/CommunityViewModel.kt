package com.buildmasterapp.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.R
import com.buildmasterapp.community.data.CommunityRepository
import com.buildmasterapp.community.data.model.Comment
import com.buildmasterapp.community.data.model.CreateCommentRequest
import com.buildmasterapp.community.data.model.CreatePostRequest
import com.buildmasterapp.community.data.model.NetworkPost
import com.buildmasterapp.community.data.model.PostItem
import com.buildmasterapp.user.data.UserTokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

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

    init {
        val tokenProvider = UserTokenProvider(application.applicationContext)
        repository = CommunityRepository(tokenProvider)
        loadPosts()
    }

    // Cargar posts desde el backend
    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val response = repository.getPosts()
            if (response.isSuccessful) {
                val posts = response.body() ?: emptyList()
                // Workaround: Mapa manual de user_id a username
                val userIdToUsername = mapOf(
                    3 to "userprueba1",
                    4 to "userprueba3"
                )
                // Mapeo de NetworkPost a PostItem para la UI usando el username del mapa
                val postItems = posts.map { networkPost ->
                    PostItem(
                        id = networkPost.id.toString(),
                        authorName = userIdToUsername[networkPost.user_id] ?: "Usuario ${networkPost.user_id}",
                        authorAvatarRes = R.drawable.ic_avatar_placeholder_1,
                        timestamp = System.currentTimeMillis(),
                        content = networkPost.content,
                        likes = networkPost.likes_count,
                        dislikes = networkPost.dislikes_count,
                        repostsCount = 0,
                        isLikedByCurrentUser = false,
                        isDislikedByCurrentUser = false,
                        comments = networkPost.comments.map { c ->
                            Comment(
                                id = c.id.toString(),
                                author = userIdToUsername[c.user_id] ?: "Usuario ${c.user_id}",
                                text = c.content,
                                timestamp = System.currentTimeMillis()
                            )
                        }.toMutableList()
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
            val response = repository.likePost(postId)
            if (response.isSuccessful) {
                loadPosts()
            } else {
                _eventChannel.send(CommunityUserEvent.ShowSnackbar("Error al dar like"))
            }
        }
    }

    // Dar dislike a un post
    fun dislikePost(postId: Int) {
        viewModelScope.launch {
            val response = repository.dislikePost(postId)
            if (response.isSuccessful) {
                loadPosts()
            } else {
                _eventChannel.send(CommunityUserEvent.ShowSnackbar("Error al dar dislike"))
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
}