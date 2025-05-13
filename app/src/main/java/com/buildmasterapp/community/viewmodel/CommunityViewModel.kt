package com.buildmasterapp.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.R
import com.buildmasterapp.community.data.model.Comment
import com.buildmasterapp.community.data.model.PostItem
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

class CommunityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<CommunityUserEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(500)
            _uiState.update {
                it.copy(posts = PostItem.getSamplePosts().sortedByDescending { post -> post.timestamp }, isLoading = false)
            }
        }
    }

    fun toggleLike(postId: String) {
        _uiState.update { currentState ->
            val updatedPosts = currentState.posts.map { post ->
                if (post.id == postId) {
                    val currentlyLiked = post.isLikedByCurrentUser
                    val currentlyDisliked = post.isDislikedByCurrentUser
                    post.copy(
                        likes = if (currentlyLiked) post.likes - 1 else post.likes + 1,
                        isLikedByCurrentUser = !currentlyLiked,
                        dislikes = if (!currentlyLiked && currentlyDisliked) post.dislikes -1 else post.dislikes,
                        isDislikedByCurrentUser = if (!currentlyLiked && currentlyDisliked) false else post.isDislikedByCurrentUser
                    )
                } else post
            }
            currentState.copy(posts = updatedPosts)
        }
    }

    fun toggleDislike(postId: String) {
        _uiState.update { currentState ->
            val updatedPosts = currentState.posts.map { post ->
                if (post.id == postId) {
                    val currentlyDisliked = post.isDislikedByCurrentUser
                    val currentlyLiked = post.isLikedByCurrentUser
                    post.copy(
                        dislikes = if (currentlyDisliked) post.dislikes - 1 else post.dislikes + 1,
                        isDislikedByCurrentUser = !currentlyDisliked,
                        likes = if (!currentlyDisliked && currentlyLiked) post.likes - 1 else post.likes,
                        isLikedByCurrentUser = if (!currentlyDisliked && currentlyLiked) false else post.isLikedByCurrentUser
                    )
                } else post
            }
            currentState.copy(posts = updatedPosts)
        }
    }

    // Modificado para añadir el comentario al post
    fun addCommentToPost(postId: String, commentText: String) {
        if (commentText.isBlank()) {
            dismissCommentDialog() // Cierra el diálogo si el comentario está vacío
            return
        }
        _uiState.update { currentState ->
            val updatedPosts = currentState.posts.map { post ->
                if (post.id == postId) {
                    val newComment = Comment(
                        author = "ErnestGreenhouse", // Usar el nombre del usuario actual
                        text = commentText
                    )
                    // Crear una nueva lista de comentarios para forzar la recomposición
                    val newCommentsList = post.comments.toMutableList().apply { add(newComment) }
                    post.copy(comments = newCommentsList)
                } else {
                    post
                }
            }
            // Cierra el diálogo después de añadir el comentario
            currentState.copy(posts = updatedPosts, commentingPostId = null, authorOfCommentingPost = null)
        }
        viewModelScope.launch {
            _eventChannel.send(CommunityUserEvent.ShowSnackbar("Comentario añadido"))
        }
    }

    fun repost(postId: String) {
        _uiState.update { currentState ->
            val updatedPosts = currentState.posts.map {
                if (it.id == postId) it.copy(repostsCount = it.repostsCount + 1) else it
            }
            currentState.copy(posts = updatedPosts)
        }
        viewModelScope.launch {
            _eventChannel.send(CommunityUserEvent.ShowSnackbar("Post reposteado (simulado)"))
        }
    }

    fun openNewPostDialog() {
        _uiState.update { it.copy(showNewPostDialog = true) }
    }

    fun dismissNewPostDialog() {
        _uiState.update { it.copy(showNewPostDialog = false) }
    }

    fun submitNewPost(content: String) {
        if (content.isNotBlank()) {
            val newPost = PostItem(
                authorName = "ErnestGreenhouse",
                authorAvatarRes = R.drawable.ic_avatar_placeholder_1,
                content = content,
                timestamp = System.currentTimeMillis()
            )
            _uiState.update { currentState ->
                currentState.copy(
                    posts = (listOf(newPost) + currentState.posts).sortedByDescending { it.timestamp },
                    showNewPostDialog = false
                )
            }
            viewModelScope.launch {
                _eventChannel.send(CommunityUserEvent.ShowSnackbar("Nuevo post creado!"))
            }
        } else {
            _uiState.update { it.copy(showNewPostDialog = false) }
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
}