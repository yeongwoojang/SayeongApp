package com.sayeong.vv.bookmark

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.sayeong.vv.model.MusicResource

@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel = hiltViewModel(),
    onMusicClick: (MusicResource) -> Unit
) {

}