package com.sayeong.vv.sayeongapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.sayeong.vv.ui.SayeongDestination

@Composable
fun rememberSayeongAppState(
    navController: NavHostController = rememberNavController()
): SayeongAppState {
    return remember(navController) {
        SayeongAppState(
            navController = navController
        )
    }
}

//_ @stable: //_ compose 컴파일러에게 해당 클래스가 Stable 상태임을 알려주는 것.
//_ 불필요한 리컴포지션을 skip하기 위해 사용
//_ stable하지 않다면 compose 컴파일러는 실제 값이 바뀌지 않았더라도 리컴포지션 시킴.
@Stable
class SayeongAppState(
    val navController: NavHostController
) {
    /**
     * 현재 화면의 NavDestination 정보를 제공
     */
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val topLevelDestinations: List<SayeongDestination> = listOf(
        SayeongDestination.HOME,
        SayeongDestination.BOOKMARK,
    )

    fun navigateToTopLevelDestination(topLevelDestination: SayeongDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        navController.navigate(topLevelDestination.route, topLevelNavOptions)
    }

    fun navigateToSearch() {
        navController.navigate("search")
    }


}