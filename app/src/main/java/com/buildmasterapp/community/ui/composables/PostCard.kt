package com.buildmasterapp.community.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildmasterapp.community.data.model.Comment
import com.buildmasterapp.community.data.model.PostItem
import com.buildmasterapp.ui.theme.AccentColor
import com.buildmasterapp.ui.theme.IconColor
import com.buildmasterapp.ui.theme.TextColorPrimary
import com.buildmasterapp.ui.theme.TextColorSecondary
import coil3.compose.rememberAsyncImagePainter


@Composable
fun PostCard(
    post: PostItem,
    onLikeClicked: (String) -> Unit,
    onDislikeClicked: (String) -> Unit,
    onCommentClicked: (String) -> Unit,
    onRepostClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var commentsExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header con gradiente sutil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar mejorado
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                    )
                                )
                            )
                    ) {
                        val avatarUrl = post.authorProfilePictureUrl
                        if (!avatarUrl.isNullOrBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(avatarUrl),
                                contentDescription = "Avatar de ${post.authorName}",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Avatar de ${post.authorName}",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        post.authorName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // Mostrar el username debajo del nombre de usuario
                            Text(
                                text = "@${it}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Botón de opciones mejorado
                    IconButton(
                        onClick = { /* TODO: opciones */ },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Más opciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Contenido del post
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Título del post
                post.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Contenido del post
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (commentsExpanded) Int.MAX_VALUE else 5,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Media gallery mejorada
                if (post.mediaUrls.isNotEmpty()) {
                    if (post.mediaUrls.size == 1) {
                        Image(
                            painter = rememberAsyncImagePainter(post.mediaUrls[0]),
                            contentDescription = "Imagen del post",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(180.dp)
                        ) {
                            items(post.mediaUrls.size) { idx ->
                                Image(
                                    painter = rememberAsyncImagePainter(post.mediaUrls[idx]),
                                    contentDescription = "Imagen ${idx + 1}",
                                    modifier = Modifier
                                        .width(240.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Barra de interacciones completamente rediseñada
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sección de likes/dislikes
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Like button mejorado
                        Surface(
                            onClick = { onLikeClicked(post.id) },
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = if (post.isLikedByCurrentUser)
                                Color(0xFFE91E63).copy(alpha = 0.1f)
                            else
                                Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (post.isLikedByCurrentUser) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Me gusta",
                                    tint = if (post.isLikedByCurrentUser) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = post.likes.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (post.isLikedByCurrentUser) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                        )

                        // Dislike button mejorado
                        Surface(
                            onClick = { onDislikeClicked(post.id) },
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = if (post.isDislikedByCurrentUser)
                                Color(0xFF2196F3).copy(alpha = 0.1f)
                            else
                                Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (post.isDislikedByCurrentUser) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                    contentDescription = "No me gusta",
                                    tint = if (post.isDislikedByCurrentUser) Color(0xFF2196F3) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = post.dislikes.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (post.isDislikedByCurrentUser) Color(0xFF2196F3) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    // Sección de repost y comentarios
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Repost button
                        Surface(
                            onClick = { onRepostClicked(post.id) },
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Repeat,
                                    contentDescription = "Repostear",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = post.repostsCount.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                        )

                        // Comment button
                        Surface(
                            onClick = { onCommentClicked(post.id) },
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = if (commentsExpanded)
                                Color(0xFFFF9800).copy(alpha = 0.1f)
                            else
                                Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (commentsExpanded) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = "Comentarios",
                                    tint = if (commentsExpanded) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = post.commentsCount.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (commentsExpanded) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            // Sección de comentarios mejorada
            if (post.comments.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    if (!commentsExpanded) {
                        TextButton(
                            onClick = { commentsExpanded = true },
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                "Ver todos los ${post.commentsCount} comentarios",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            post.comments.forEach { comment ->
                                CommentView(comment)
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Botón para ocultar comentarios
                            TextButton(
                                onClick = { commentsExpanded = false },
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    "Ocultar comentarios",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CommentView(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar del comentario
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        ) {
                            append(comment.author ?: "Usuario")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun CreatePostForm(onPostCreated: (String, String, List<String>) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var mediaUrls by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Contenido") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = mediaUrls,
            onValueChange = { mediaUrls = it },
            label = { Text("URLs de medios (separados por comas)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val mediaUrlList = mediaUrls.split(",").map { it.trim() }
            onPostCreated(title, content, mediaUrlList)
        }, modifier = Modifier.align(Alignment.End)) {
            Text("Publicar")
        }
    }
}
