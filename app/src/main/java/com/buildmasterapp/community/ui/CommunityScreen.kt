package com.buildmasterapp.community.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buildmasterapp.community.ui.composables.CommentInputDialog
import com.buildmasterapp.community.ui.composables.NewPostInputDialog
import com.buildmasterapp.community.ui.composables.PostCard
import com.buildmasterapp.community.viewmodel.CommunityUiState
import com.buildmasterapp.community.viewmodel.CommunityUserEvent
import com.buildmasterapp.community.viewmodel.CommunityViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    communityViewModel: CommunityViewModel = viewModel()
) {
    val uiState by communityViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        communityViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CommunityUserEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    if (uiState.showNewPostDialog) {
        NewPostInputDialog(
            onDismissRequest = { communityViewModel.dismissNewPostDialog() },
            onConfirm = { title, content, mediaUrls ->
                communityViewModel.publishPost(
                    title = title,
                    content = content,
                    mediaUrls = mediaUrls
                )
                communityViewModel.dismissNewPostDialog() // Cerrar el diálogo después de publicar
            }
        )
    }

    // Mostrar el diálogo de comentario si commentingPostId no es nulo
    uiState.commentingPostId?.let { postId ->
        uiState.authorOfCommentingPost?.let { authorName ->
            CommentInputDialog(
                postAuthor = authorName,
                onDismissRequest = { communityViewModel.dismissCommentDialog() },
                onConfirm = { commentText ->
                    postId.toIntOrNull()?.let { intId ->
                        communityViewModel.commentOnPost(intId, commentText)
                        communityViewModel.dismissCommentDialog() // Cierra el diálogo al comentar
                    }
                }
            )
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { /* perfil */ }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Avatar de usuario")
                    }
                },
                title = { Text("Comunidad BuildMaster") },
                actions = {
                    IconButton(onClick = { /* buscar */ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = { /* notificaciones */ }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { communityViewModel.openNewPostDialog() }) {
                Icon(Icons.Filled.Add, contentDescription = "Crear nuevo post")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contenido del feed
            CommunityScreenContent(
                modifier = Modifier.fillMaxSize(),
                uiState = uiState,
                communityViewModel = communityViewModel
            )
        }
    }
}

@Composable
fun CommunityScreenContent(
    modifier: Modifier = Modifier,
    uiState: CommunityUiState,
    communityViewModel: CommunityViewModel
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            }
        } else if (uiState.posts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no hay posts. ¡Sé el primero!")
            }
        }
        else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(uiState.posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onLikeClicked = { post.id.toIntOrNull()?.let { communityViewModel.likePost(it) } },
                        onDislikeClicked = { post.id.toIntOrNull()?.let { communityViewModel.dislikePost(it) } },
                        onCommentClicked = { communityViewModel.openCommentDialog(post.id) },
                        onRepostClicked = { post.id.toIntOrNull()?.let { communityViewModel.repost(it) } }
                    )
                }
            }
        }
    }
}