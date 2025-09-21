package com.sayeong.vv.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.search.SearchScreen
import com.sayeong.vv.ui.SayeongDestination
import kotlinx.serialization.Serializable


fun NavController.navigateToSearch(navOptions: NavOptions? = null) =
    navigate(SayeongDestination.SEARCH.route, navOptions)

fun NavGraphBuilder.searchScreen(
    onBackClick: () -> Unit,
    onMusicPlay: (List<MusicResource>) -> Unit
) {
    composable(SayeongDestination.SEARCH.route) {
        SearchScreen(
            onBackClick = onBackClick,
            onMusicPlay = onMusicPlay,
        )
    }
}