package com.buildmasterapp.community.data.model

/**
 * Representa la solicitud para actualizar un post.
 */
data class UpdatePostRequest(
    val title: String,
    val content: String,
    val mediaUrls: List<String>
)
