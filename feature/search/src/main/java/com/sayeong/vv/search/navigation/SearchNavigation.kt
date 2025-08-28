package com.sayeong.vv.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sayeong.vv.search.SearchScreen
import kotlinx.serialization.Serializable

private const val searchRoute = "Search"

fun NavController.navigateToSearch(navOptions: NavOptions? = null) =
    navigate(searchRoute, navOptions)

fun NavGraphBuilder.searchScreen(
    onBackClick: () -> Unit,
) {
    composable(searchRoute) {
        SearchScreen(
            onBackClick = onBackClick
        )

    }
}