package com.sayeong.vv.bookmark.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sayeong.vv.bookmark.BookmarkScreen
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.ui.SayeongDestination

fun NavGraphBuilder.bookmarkScreen(
    onMusicPlay: (List<MusicResource>) -> Unit
) {
    composable(SayeongDestination.BOOKMARK.route) {
        BookmarkScreen(
            onMusicPlay = onMusicPlay,
        )
    }
}