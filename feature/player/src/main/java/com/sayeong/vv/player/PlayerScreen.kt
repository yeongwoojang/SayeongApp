package com.sayeong.vv.player

import MiniPlayerScreen
import androidx.annotation.OptIn
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import com.sayeong.vv.player.component.ExtraControls
import com.sayeong.vv.player.model.LoadedState
import com.sayeong.vv.player.model.PlayerState
import timber.log.Timber
import java.util.concurrent.TimeUnit

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    targetSheetState: SheetValue,
    onExpand : () -> Unit
) {
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    when (val state = playerState) {
        is PlayerState.Idle -> {
            Box(modifier = Modifier.fillMaxHeight())
        }
        is LoadedState -> {
            Crossfade(
                targetState = targetSheetState,
                label = "PlayerUI"
            ) { targetState ->
                if (targetState == SheetValue.Expanded) {
                    //_ 전체 화면 플레이어
                    PlayerContent(
                        player = viewModel.player,
                        onPause = viewModel::onPause,
                        onSeekTo = viewModel::seekTo,
                        state = state
                    )
                } else {
                    //_ 미니 플레이어
                    MiniPlayerScreen(
                        state = state,
                        player = viewModel.player,
                        onExpand = onExpand
                    )
                }
            }
        }
        else -> {}
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PlayerContent(
    player: Player,
    onPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    state: LoadedState
) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isUserSeeking by remember { mutableStateOf(false) }

    val dominantColor = state.dominantColor ?: Color.Black
    val gradientColor = state.gradientColor ?: dominantColor

    LaunchedEffect(state.currentPosition) {
        if (!isUserSeeking) {
            sliderPosition = state.currentPosition.toFloat()
        }
    }

//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            if (event == Lifecycle.Event.ON_PAUSE) {
//                onPause()
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        dominantColor.copy(alpha = 0.8f), //_ 위쪽 색상
                        gradientColor.copy(alpha = 0.6f), //_ 아래쪽 색상
                        dominantColor.copy(alpha = 0.8f)
                    )
                )
            )

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) //_ 남는 공간을 모두 차지
        ) {
            PlayerSurface(
                player,
                surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            )
            state.albumArt?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "${state.musicResource.originalName} 앨범 아트",
                    modifier = Modifier.fillMaxSize()
                        .padding(12.dp)
                        .background(color = Color.Transparent, shape = RoundedCornerShape(16.dp))
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // 하단 네비게이션 바 영역 확보
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.musicResource.originalName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = state.musicResource.artist ?: "Unknown Artist",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            PlayerBar(
                modifier = Modifier.padding(top = 16.dp),
                sliderPosition = sliderPosition,
                onValueChange = { newPosition ->
                    isUserSeeking = true
                    sliderPosition = newPosition
                },
                onValueChangeFinished = {
                    onSeekTo(sliderPosition.toLong())
                    isUserSeeking = false
                },
                duration = state.duration
            )

            ExtraControls(
                modifier = Modifier.padding(top = 8.dp),
                player = player,
            )
        }
    }
}

@Composable
private fun PlayerBar(
    modifier: Modifier,
    sliderPosition: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    duration: Long
) {
    Column(
        modifier = modifier
    ) {
        Slider(
            value = sliderPosition, // 로컬 상태 사용
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            // 5. uiState 대신 전달받은 state의 속성을 사용합니다.
            valueRange = 0f..duration.toFloat().coerceAtLeast(0f),
            modifier = Modifier.fillMaxWidth(),
            track = { sliderState ->
                // sliderState에서 현재 진행률(fraction)을 가져올 수 있습니다.
                val progress = sliderState.value / sliderState.valueRange.endInclusive

                // 비활성 트랙 (전체 길이의 회색 막대)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSecondary, //_비활성 트랙 색상
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress) //_ 진행률만큼 너비를 채움
                                .height(4.dp)
                                .background(
                                    color = Color.Red, //_활성 트랙 색상
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            },
            thumb = {
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(12.dp)
                            .background(
                                color = Color.Red, // 색상
                                shape = CircleShape // 모양
                            )
                    )
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 6. 시간 표시는 드래그 중인 로컬 값(sliderPosition)으로 표시
            Text(
                text = formatDuration(sliderPosition.toLong()),
                style = MaterialTheme.typography.labelSmall


            )
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.labelSmall

            )
        }
    }
}


private fun formatDuration(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
