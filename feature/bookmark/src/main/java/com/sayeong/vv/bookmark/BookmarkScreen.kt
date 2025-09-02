package com.sayeong.vv.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.ui.MusicUiModel
import com.sayeong.vv.ui.components.MusicItem

@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel = hiltViewModel(),
    onMusicClick: (MusicResource) -> Unit
) {

    val bookmarkUiState by viewModel.bookmarkUiState.collectAsStateWithLifecycle()

    when (val state = bookmarkUiState) {
        is BookmarkUIState.Loading -> {
            CircularProgressIndicator()
        }
        is BookmarkUIState.Shown -> {
            if (state.musicResources.isEmpty()) {
                Box(modifier = Modifier
                    .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "북마크 된 음악이 없습니다.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                BookmarkContent(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 24.dp),
                    musics = state.musicResources,
                    onMusicClick = onMusicClick,
                    onToggleBookMark = viewModel::toggleBookMark
                )
            }
        }
        else -> {}
    }

}

@Composable
fun BookmarkContent(
    modifier: Modifier,
    musics: List<MusicUiModel>,
    onMusicClick: (MusicResource) -> Unit,
    onToggleBookMark: (MusicResource) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        items(
            items = musics,
            key = { it.id }
        ) { music ->
            MusicItem(
                isBookmarked = true,
                musicUiModel = music,
                modifier = Modifier,
                onToggleBookMark = onToggleBookMark,
                onMusicClick = { onMusicClick(music.musicResource) }
            )
        }
    }
}