package com.buildmasterapp.community.data.model

import androidx.annotation.DrawableRes
import com.buildmasterapp.R // Asegúrate de la ruta a tu R
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Nueva data class para un comentario individual
data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val author: String,
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
    val authorName: String,
    @DrawableRes val authorAvatarRes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val content: String,
    var likes: Int = 0,
    var dislikes: Int = 0,
    var repostsCount: Int = 0,
    var isLikedByCurrentUser: Boolean = false,
    var isDislikedByCurrentUser: Boolean = false,
    val comments: MutableList<Comment> = mutableListOf() // Lista de comentarios
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
                    authorName = "Usuario Alfa",
                    authorAvatarRes = R.drawable.ic_avatar_placeholder_1,
                    content = "¡Hola comunidad! Compartiendo mi última configuración de PC. ¡Es una bestia para gaming y desarrollo! #PCMasterRace #Build",
                    likes = 15,
                    dislikes = 1,
                    repostsCount = 2,
                    comments = mutableListOf(
                        Comment(author = "Beta Tester", text = "¡Se ve genial! ¿Qué GPU usaste?"),
                        Comment(author = "Gaming Guru", text = "Impresionante build.")
                    )
                ),
                PostItem(
                    authorName = "Beta Tester",
                    authorAvatarRes = R.drawable.ic_avatar_placeholder_2,
                    content = "¿Alguien ha probado la nueva RTX 4070 Super? Buscando opiniones antes de comprar. Gracias de antemano. 🙏",
                    likes = 22,
                    dislikes = 0,
                    repostsCount = 1,
                    isLikedByCurrentUser = true,
                    comments = mutableListOf(
                        Comment(author = "ErnestGreenhouse", text = "Yo la tengo, ¡es una maravilla para 1440p!")
                    )
                ),
                PostItem(
                    authorName = "Gaming Guru",
                    authorAvatarRes = R.drawable.ic_avatar_placeholder_3,
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