package com.sayeong.vv.sayeongapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sayeong.vv.home.BookMarkScreen
import com.sayeong.vv.home.HomeScreen
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.search.SearchScreen

@Composable
fun SayeongNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onMusicClick: (MusicResource) -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable(SayeongDestination.HOME.route) {
            HomeScreen(onMusicClick = onMusicClick)
        }

        composable(SayeongDestination.BOOKMARK.route) {
            BookMarkScreen()
        }

        composable(SayeongDestination.SEARCH.route) {
            SearchScreen()
        }
    }
}