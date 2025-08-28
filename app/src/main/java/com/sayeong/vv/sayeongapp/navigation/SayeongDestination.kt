package com.sayeong.vv.sayeongapp.navigation

import androidx.annotation.DrawableRes
import com.sayeong.vv.sayeongapp.R

sealed class SayeongDestination(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
){
    object HOME: SayeongDestination(
        route = "HOME",
        label = "Home",
        icon = R.drawable.home_24px
    )
    object BOOKMARK: SayeongDestination(
        route = "BOOKMARK",
        label = "Bookmark",
        icon = R.drawable.bookmark_24px

    )
    object SEARCH: SayeongDestination(
        route = "SEARCH",
        label = "Search",
        icon = R.drawable.search_24px
    )
}
