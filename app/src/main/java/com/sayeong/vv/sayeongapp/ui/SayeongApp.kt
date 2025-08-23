package com.sayeong.vv.sayeongapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sayeong.vv.designsystem.component.SayeongBackground
import com.sayeong.vv.designsystem.component.SayeongGradientBackground
import com.sayeong.vv.designsystem.theme.LocalGradientColors
import com.sayeong.vv.sayeongapp.navigation.SayeongDestination
import com.sayeong.vv.sayeongapp.navigation.SayeongNavHost

@Composable
fun SayeongApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val destinations = listOf(
        SayeongDestination.HOME,
        SayeongDestination.BOOKMARK,
        SayeongDestination.SEARCH
    )


    SayeongBackground(modifier = Modifier) {
        SayeongGradientBackground(gradientColors = LocalGradientColors.current) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    NavigationBar {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        destinations.forEach { destination ->
                            val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(destination.route) {
                                        //_ 백스택 관리를 위한 로직
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = destination.icon),
                                        contentDescription = destination.label
                                    )
                                },
                                label = { Text(destination.label) }

                            )

                        }
                    }
                }
            ) { innerPadding ->
                SayeongNavHost(
                    modifier = Modifier.padding(innerPadding),
                    navController
                )
            }
        }
    }
}