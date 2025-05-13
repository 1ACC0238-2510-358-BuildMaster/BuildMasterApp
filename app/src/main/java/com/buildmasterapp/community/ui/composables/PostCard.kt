package com.buildmasterapp.community.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ThumbDownOffAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildmasterapp.community.data.model.Comment
import com.buildmasterapp.community.data.model.PostItem


@Composable
fun PostCard(
    post: PostItem,
    onLikeClicked: (String) -> Unit,
    onDislikeClicked: (String) -> Unit,
    onCommentClicked: (String) -> Unit, // Se usará para abrir el diálogo
    onRepostClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Author Row (sin cambios)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = post.authorAvatarRes),
                    contentDescription = "${post.authorName} avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(post.authorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    Text(post.getFormattedTimestamp(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Content (sin cambios)
            Text(post.content, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))

            // Sección de comentarios (muestra los últimos 2 o un mensaje)
            if (post.comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Text("Comentarios:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))
                // Muestra los últimos 2 comentarios, por ejemplo
                post.comments.takeLast(2).forEach { comment ->
                    CommentView(comment)
                }
                if (post.comments.size > 2) {
                    TextButton(onClick = { /* TODO: Abrir pantalla de todos los comentarios */ }) {
                        Text("Ver todos los ${post.comments.size} comentarios")
                    }
                }
                Divider(modifier = Modifier.padding(top = 4.dp))
                Spacer(modifier = Modifier.height(8.dp))
            }


            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostActionButton(
                    iconFilled = Icons.Filled.Favorite,
                    iconOutlined = Icons.Outlined.FavoriteBorder,
                    text = post.likes.toString(),
                    isSelected = post.isLikedByCurrentUser,
                    onClick = { onLikeClicked(post.id) },
                    selectedColor = Color.Red
                )
                PostActionButton(
                    iconFilled = Icons.Filled.ThumbDown,
                    iconOutlined = Icons.Outlined.ThumbDownOffAlt,
                    text = post.dislikes.toString(),
                    isSelected = post.isDislikedByCurrentUser,
                    onClick = { onDislikeClicked(post.id) },
                    selectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PostActionButton(
                    iconFilled = Icons.Filled.ChatBubble,
                    iconOutlined = Icons.Outlined.ChatBubbleOutline,
                    text = post.commentsCount.toString(), // Usa la propiedad derivada
                    isSelected = false,
                    onClick = { onCommentClicked(post.id) } // Llama a la función que abre el diálogo
                )
                PostActionButton(
                    iconFilled = Icons.Filled.Repeat,
                    iconOutlined = Icons.Filled.Repeat,
                    text = post.repostsCount.toString(),
                    isSelected = false,
                    onClick = { onRepostClicked(post.id) }
                )
            }
        }
    }
}

@Composable
private fun CommentView(comment: Comment) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        // Podrías añadir un avatar pequeño para el autor del comentario aquí
        Column {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(comment.author)
                    }
                    append("  ") // Espacio
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)) {
                        append(comment.getFormattedTimestamp())
                    }
                },
                style = MaterialTheme.typography.labelLarge
            )
            Text(comment.text, style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
private fun PostActionButton(
    iconFilled: androidx.compose.ui.graphics.vector.ImageVector,
    iconOutlined: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color = MaterialTheme.colorScheme.primary
) {
    val icon = if (isSelected) iconFilled else iconOutlined
    val tint = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        IconToggleButton(
            checked = isSelected,
            onCheckedChange = { onClick() }
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = tint)
        }
        if (text != "0" || isSelected || icon == Icons.Outlined.ChatBubbleOutline || icon == Icons.Filled.ChatBubble) { // Muestra siempre para comentarios
            Text(
                text = text,
                color = tint,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 0.dp, end = 4.dp)
            )
        }
    }
}