package com.buildmasterapp.community.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.buildmasterapp.community.data.model.Comment
import com.buildmasterapp.community.data.model.PostItem
import com.buildmasterapp.ui.theme.AccentColor
import com.buildmasterapp.ui.theme.IconColor
import com.buildmasterapp.ui.theme.TextColorPrimary
import com.buildmasterapp.ui.theme.TextColorSecondary
import coil.compose.rememberAsyncImagePainter


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
            .padding(vertical = 8.dp, horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar y username
                val avatarUrl = post.authorProfilePictureUrl
                if (!avatarUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(avatarUrl),
                        contentDescription = "Avatar de ${post.authorName}",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar de ${post.authorName}",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    post.authorName?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color.Black
                            )
                        )
                    }
                    Text(
                        post.getFormattedTimestamp(),
                        style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                }
                // Opcional: icono de opciones
                IconButton(onClick = { /* TODO: opciones */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Título del post (si existe)
            post.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Contenido del post
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = androidx.compose.ui.graphics.Color.Black
                ),
                maxLines = if (commentsExpanded) Int.MAX_VALUE else 5,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Media gallery
            if (post.mediaUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                if (post.mediaUrls.size == 1) {
                    Image(
                        painter = rememberAsyncImagePainter(post.mediaUrls[0]),
                        contentDescription = "Imagen del post",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(160.dp)
                    ) {
                        items(post.mediaUrls.size) { idx ->
                            Image(
                                painter = rememberAsyncImagePainter(post.mediaUrls[idx]),
                                contentDescription = "Imagen ${idx + 1}",
                                modifier = Modifier
                                    .width(220.dp)
                                    .fillMaxHeight()
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Engagement bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onLikeClicked(post.id) }) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Me gusta",
                            tint = if (post.isLikedByCurrentUser) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.Gray
                        )
                    }
                    Text(post.likes.toString(), color = androidx.compose.ui.graphics.Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(onClick = { onDislikeClicked(post.id) }) {
                        Icon(
                            imageVector = Icons.Filled.ThumbDown,
                            contentDescription = "No me gusta",
                            tint = if (post.isDislikedByCurrentUser) androidx.compose.ui.graphics.Color(0xFF1565C0) else androidx.compose.ui.graphics.Color.Gray
                        )
                    }
                    Text(post.dislikes.toString(), color = androidx.compose.ui.graphics.Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(onClick = { onRepostClicked(post.id) }) {
                        Icon(
                            imageVector = Icons.Filled.Repeat,
                            contentDescription = "Repostear",
                            tint = androidx.compose.ui.graphics.Color(0xFF43A047)
                        )
                    }
                    Text(post.repostsCount.toString(), color = androidx.compose.ui.graphics.Color.Black)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onCommentClicked(post.id) }) {
                        Icon(
                            imageVector = Icons.Filled.ChatBubble,
                            contentDescription = "Comentarios",
                            tint = if (commentsExpanded) androidx.compose.ui.graphics.Color(0xFFFB8C00) else androidx.compose.ui.graphics.Color.Gray
                        )
                    }
                    Text(post.commentsCount.toString(), color = androidx.compose.ui.graphics.Color.Black)
                }
            }

            // Comments section
            if (post.comments.isNotEmpty()) {
                if (!commentsExpanded) {
                    TextButton(onClick = { commentsExpanded = true }) {
                        Text("Ver todos los ${post.commentsCount} comentarios", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        post.comments.forEach { comment ->
                            CommentView(comment)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentView(comment: Comment) {
    Row {
        Column {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Black)) {
                        append(comment.author ?: "")
                    }
                    append(": ")
                    withStyle(style = SpanStyle(color = androidx.compose.ui.graphics.Color.Black)) {
                        append(comment.text)
                    }
                },
                style = MaterialTheme.typography.bodyLarge
            )
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
