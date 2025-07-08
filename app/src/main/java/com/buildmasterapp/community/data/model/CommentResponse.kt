package com.buildmasterapp.community.data.model

/**
 * Representa la respuesta de un comentario.
 */
data class CommentResponse(
    val id: Int,
    val content: String,
    val userId: Int,
    val postId: Int,
    val username: String
)
