package com.sayeong.vv.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sayeong.vv.home.HomeScreen
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.ui.SayeongDestination

fun NavGraphBuilder.homeScreen(
    onMusicClick: (MusicResource) -> Unit
) {
    composable(SayeongDestination.HOME.route) {
        HomeScreen(onMusicClick = onMusicClick)

    }
}