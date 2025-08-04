package com.sayeong.vv.sayeongapp.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
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
    object SECOND: SayeongDestination(
        route = "second",
        label = "Second",
        icon = R.drawable.home_24px

    )
    object Third: SayeongDestination(
        route = "third",
        label = "Third",
        icon = R.drawable.home_24px
    )
}
