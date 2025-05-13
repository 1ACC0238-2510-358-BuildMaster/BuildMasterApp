package com.buildmasterapp.community.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
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
            onConfirm = { content ->
                communityViewModel.submitNewPost(content)
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
                    communityViewModel.addCommentToPost(postId, commentText)
                }
            )
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Comunidad BuildMaster") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { communityViewModel.openNewPostDialog() }) {
                Icon(Icons.Filled.Add, contentDescription = "Crear nuevo post")
            }
        }
    ) { paddingValues ->
        CommunityScreenContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            viewModel = communityViewModel
        )
    }
}

@Composable
fun CommunityScreenContent(
    modifier: Modifier = Modifier,
    uiState: CommunityUiState,
    viewModel: CommunityViewModel
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
                        onLikeClicked = { viewModel.toggleLike(post.id) },
                        onDislikeClicked = { viewModel.toggleDislike(post.id) },
                        onCommentClicked = { viewModel.openCommentDialog(post.id) }, // Abre el diálogo
                        onRepostClicked = { viewModel.repost(post.id) }
                    )
                }
            }
        }
    }
}