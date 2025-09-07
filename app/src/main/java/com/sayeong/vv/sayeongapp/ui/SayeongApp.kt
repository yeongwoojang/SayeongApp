package com.sayeong.vv.sayeongapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import com.sayeong.vv.designsystem.component.SayeongBackground
import com.sayeong.vv.designsystem.component.SayeongGradientBackground
import com.sayeong.vv.designsystem.theme.LocalGradientColors
import com.sayeong.vv.player.PlayerScreen
import com.sayeong.vv.player.PlayerViewModel
import com.sayeong.vv.player.model.PlayerState
import com.sayeong.vv.sayeongapp.navigation.SayeongNavHost
import kotlinx.coroutines.launch

@Composable
fun SayeongApp(
    appState: SayeongAppState,
    modifier: Modifier = Modifier
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val playerState by playerViewModel.playerState.collectAsStateWithLifecycle()

    val currentDestination = appState.currentDestination
    val currentTopLevelDestination = appState.topLevelDestinations.find { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    }

    var isPlayerLaunched by rememberSaveable { mutableStateOf(false) }
    if (playerState !is PlayerState.Idle) {
        isPlayerLaunched = true
    }

    // isPlayerLaunched 상태에 따라 peekHeight를 동적으로 결정
    val sheetPeekHeight = if (isPlayerLaunched) 80.dp else 0.dp


    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        when(scaffoldState.bottomSheetState.currentValue) {
            SheetValue.Expanded -> {

            }
            SheetValue.PartiallyExpanded -> {

            }
            else -> {}
        }
    }

    SayeongBackground(modifier = Modifier) {
        SayeongGradientBackground(gradientColors = LocalGradientColors.current) {
            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetContent = {
                    PlayerScreen(
                        viewModel = playerViewModel,
                        targetSheetState = scaffoldState.bottomSheetState.targetValue,
                        onExpand = {
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    )
                },
                sheetPeekHeight = sheetPeekHeight,
                containerColor = Color.Transparent,
                sheetShape = RectangleShape,
                sheetDragHandle = {}
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