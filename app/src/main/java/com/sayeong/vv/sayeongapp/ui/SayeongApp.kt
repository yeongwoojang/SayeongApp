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
    shouldLaunchPlayer: Boolean, // MainActivity로부터 받을 신호
    onPlayerLaunched: () -> Unit, // 실행 완료 후 MainActivity에 알릴 콜백
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

    LaunchedEffect(shouldLaunchPlayer) {
        if (shouldLaunchPlayer) {
            scope.launch {
                scaffoldState.bottomSheetState.expand()
                onPlayerLaunched() // 실행 완료 콜백 호출
            }
        }
    }


    var isPlayerLaunched by rememberSaveable { mutableStateOf(false) }
    if (playerState !is PlayerState.Idle) {
        isPlayerLaunched = true
    }

    val sheetPeekHeight = if (isPlayerLaunched) 172.dp else 0.dp

    SayeongBackground(modifier = modifier) {
        SayeongGradientBackground(gradientColors = LocalGradientColors.current) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    if (currentTopLevelDestination != null
                        && scaffoldState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "${currentTopLevelDestination.label}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.Black,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { appState.navigateToSearch() }) {
                                    Icon(
                                        painter = painterResource(id = com.sayeong.vv.ui.R.drawable.search_24px),
                                        contentDescription = "Search"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {}) {
                                    Icon(
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
                    if (scaffoldState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded) {
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
                }
            ) { innerScaffoldPadding ->
                BottomSheetScaffold(
                    modifier = Modifier.padding(top = innerScaffoldPadding.calculateTopPadding()),
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
                    sheetDragHandle = {},
                ) { innerBottomSheetPadding ->
                    SayeongNavHost(
                        modifier = Modifier.padding(innerBottomSheetPadding),
                        navController = appState.navController,
                        onMusicClick = { music ->
//                            playerViewModel.playMusic(music)
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                        onMusicPlay = { musics ->
                            playerViewModel.playMusics(musics)
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