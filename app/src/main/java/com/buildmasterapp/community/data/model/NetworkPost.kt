package com.buildmasterapp.community.data.model

// Modelo de comentario compatible con el backend
// (diferente del modelo local para UI)
data class NetworkComment(
    val id: Int,
    val user_id: Int,
    val content: String,
    val username: String? = null
)

// Modelo de post compatible con el backend
data class NetworkPost(
    val id: Int,
    val user_id: Int,
    val title: String,
    val content: String,
    val media_urls: List<String> = emptyList(),
    val original_post_id: Int? = null,
    val likes_count: Int = 0,
    val dislikes_count: Int = 0,
    val comments: List<NetworkComment> = emptyList(),
    val username: String? = null
)

// Modelo para crear un post (request)
data class CreatePostRequest(
    val title: String,
    val content: String,
    val media_urls: List<String> = emptyList()
)

// Modelo para comentar un post (request)
data class CreateCommentRequest(
    val content: String
)
