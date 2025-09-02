package com.sayeong.vv.ui

import androidx.annotation.DrawableRes

sealed class SayeongDestination(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
){
    data object HOME: SayeongDestination(
        route = "HOME",
        label = "Home",
        icon = R.drawable.home_24px
    )
    data object BOOKMARK: SayeongDestination(
        route = "BOOKMARK",
        label = "Bookmark",
        icon = R.drawable.bookmark_24px

    )
    data object SEARCH: SayeongDestination(
        route = "SEARCH",
        label = "Search",
        icon = R.drawable.search_24px
    )
}