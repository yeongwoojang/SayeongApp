package com.sayeong.vv.sayeongapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.sayeong.vv.bookmark.navigation.bookmarkScreen
import com.sayeong.vv.home.navigation.homeScreen
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.search.navigation.searchScreen

@Composable
fun SayeongNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onMusicClick: (MusicResource) -> Unit,
    onMusicPlay: (List<MusicResource>) -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        homeScreen(
            onMusicClick = onMusicClick,
            onMusicPlay = onMusicPlay
        )

        searchScreen(
            onBackClick = navController::popBackStack,
            onMusicClick = onMusicClick
        )

        bookmarkScreen(
            onMusicClick = onMusicClick
        )
    }
}