package com.sayeong.vv.sayeongapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sayeong.vv.designsystem.component.SayeongBackground
import com.sayeong.vv.designsystem.component.SayeongGradientBackground
import com.sayeong.vv.designsystem.theme.LocalGradientColors
import com.sayeong.vv.player.PlayerSection
import com.sayeong.vv.player.PlayerViewModel
import com.sayeong.vv.sayeongapp.R
import com.sayeong.vv.sayeongapp.navigation.SayeongNavHost
import kotlinx.coroutines.launch

@Composable
fun SayeongApp(
    appState: SayeongAppState,
    modifier: Modifier = Modifier
) {
    // 1. BottomSheet의 상태를 관리할 State와 CoroutineScope를 생성합니다.
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val playerViewModel: PlayerViewModel = hiltViewModel()

    val currentDestination = appState.currentDestination
    val currentTopLevelDestination = appState.topLevelDestinations.find { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    }

    SayeongBackground(modifier = Modifier) {
        SayeongGradientBackground(gradientColors = LocalGradientColors.current) {
            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetContent = {
                    PlayerSection(viewModel = playerViewModel)
                },
                sheetPeekHeight = 0.dp, //_ 초기에는 시트 미노출
                containerColor = Color.Transparent,
            ) { innerPadding ->
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        if (currentTopLevelDestination != null) {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "${currentTopLevelDestination?.label}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.Black,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        appState.navigateToSearch()
                                    }) {
                                        Icon(
                                            //TODO 이미지 한곳에서 모아서 쓸 수 있도록 수정 필요
                                            painter = painterResource(id = com.sayeong.vv.ui.R.drawable.search_24px),
                                            contentDescription = "Search"
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {}) {
                                        Icon(
                                            //TODO 셋팅 이미지 노출 필요 (테마 설정 기능 추가 예정)
                                            painter = painterResource(id = com.sayeong.vv.ui.R.drawable.bookmark_24px),
                                            contentDescription = "Settings"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent,
                                    titleContentColor = Color.Transparent
                                )
                            )
                        }
                    },
                    bottomBar = {
                        NavigationBar {
                            appState.topLevelDestinations.forEach { destination ->
                                val selected =
                                    currentDestination?.hierarchy?.any { it.route == destination.route } == true
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { appState.navigateToTopLevelDestination(destination) },
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
                ) { innerScaffoldPadding ->
                    SayeongNavHost(
                        modifier = Modifier.padding(innerScaffoldPadding),
                        appState.navController,
                        onMusicClick = { music ->
                            playerViewModel.playMusic(music)
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    )
                }
            }
        }
    }
}