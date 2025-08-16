package com.sayeong.vv.sayeongapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sayeong.vv.home.HomeScreen
import com.sayeong.vv.home.ThirdScreen
import com.sayeong.vv.player.PlayerScreen

@Composable
fun SayeongNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable(SayeongDestination.HOME.route) {
            HomeScreen()
        }

        composable(SayeongDestination.SECOND.route) {
            PlayerScreen()
        }

        composable(SayeongDestination.Third.route) {
            ThirdScreen()
        }
    }
}