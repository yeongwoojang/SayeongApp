package com.sayeong.vv.sayeongapp.navigation

import androidx.annotation.DrawableRes
import com.sayeong.vv.sayeongapp.R

sealed class SayeongDestination(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
){
    object HOME: SayeongDestination(
        route = "home",
        label = "Home",
        icon = R.drawable.home_24px
    )
    object BOOKMARK: SayeongDestination(
        route = "bookmark",
        label = "Bookmark",
        icon = com.sayeong.vv.home.R.drawable.bookmark_24px

    )
    object SEARCH: SayeongDestination(
        route = "search",
        label = "Search",
        icon = com.sayeong.vv.home.R.drawable.search_24px
    )
}
