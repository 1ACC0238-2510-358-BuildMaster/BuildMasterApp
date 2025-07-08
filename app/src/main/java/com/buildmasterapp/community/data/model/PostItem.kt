package com.buildmasterapp.community.data.model

import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Nueva data class para un comentario individual
data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val author: String?,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

data class PostItem(
    val id: String = UUID.randomUUID().toString(),
    val authorId: Int, // user_id del autor
    val authorName: String?,
    val email: String? = null, // Campo para el email
    val authorProfilePictureUrl: String? = null, // URL del avatar
    val title: String? = null, // Nuevo campo para el título
    val timestamp: Long = System.currentTimeMillis(),
    val content: String,
    var likes: Int = 0,
    var dislikes: Int = 0,
    var repostsCount: Int = 0,
    var isLikedByCurrentUser: Boolean = false,
    var isDislikedByCurrentUser: Boolean = false,
    val comments: MutableList<Comment> = mutableListOf(),
    val mediaUrls: List<String> = emptyList()
) {
    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("dd MMM 'at' HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // El contador de comentarios ahora se deriva del tamaño de la lista
    val commentsCount: Int
        get() = comments.size

    companion object {
        fun getSamplePosts(): List<PostItem> {
            return listOf(
                PostItem(
                    authorId = 1,
                    authorName = "Usuario Alfa",
                    authorProfilePictureUrl = "https://randomuser.me/api/portraits/men/1.jpg",
                    content = "¡Hola comunidad! Compartiendo mi última configuración de PC. ¡Es una bestia para gaming y desarrollo! #PCMasterRace #Build",
                    likes = 15,
                    dislikes = 1,
                    repostsCount = 2,
                    comments = mutableListOf(
                        Comment(author = "Beta Tester", text = "¡Se ve genial! ¿Qué GPU usaste?"),
                        Comment(author = "Gaming Guru", text = "Impresionante build.")
                    ),
                    mediaUrls = listOf(
                        "https://example.com/image1.jpg",
                        "https://example.com/image2.jpg"
                    )
                ),
                PostItem(
                    authorId = 2,
                    authorName = "Beta Tester",
                    authorProfilePictureUrl = "https://randomuser.me/api/portraits/men/2.jpg",
                    content = "¿Alguien ha probado la nueva RTX 4070 Super? Buscando opiniones antes de comprar. Gracias de antemano. 🙏",
                    likes = 22,
                    dislikes = 0,
                    repostsCount = 1,
                    isLikedByCurrentUser = true,
                    comments = mutableListOf(
                        Comment(author = "ErnestGreenhouse", text = "Yo la tengo, ¡es una maravilla para 1440p!")
                    ),
                    mediaUrls = listOf(
                        "https://example.com/image3.jpg"
                    )
                ),
                PostItem(
                    authorId = 3,
                    authorName = "Gaming Guru",
                    authorProfilePictureUrl = "https://randomuser.me/api/portraits/men/3.jpg",
                    content = "Recordatorio amistoso: ¡Limpien sus ventiladores! Un buen flujo de aire es clave para el rendimiento y la longevidad de sus componentes. 💨💻",
                    likes = 50,
                    dislikes = 2,
                    repostsCount = 5
                )
                // Puedes añadir más posts de ejemplo
            )
        }
    }
}