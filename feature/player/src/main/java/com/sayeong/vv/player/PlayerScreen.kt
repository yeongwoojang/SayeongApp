package com.sayeong.vv.player

import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import com.sayeong.vv.player.component.ExtraControls
import com.sayeong.vv.player.component.noRippleClickable
import com.sayeong.vv.player.model.LoadedState
import com.sayeong.vv.player.model.PlayerState
import java.util.concurrent.TimeUnit

@OptIn(UnstableApi::class)
@Composable
fun PlayerSection(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    var showControls by remember { mutableStateOf(true) }
    var sliderPosition by remember { mutableStateOf(0f) }

    //_ 사용자가 Slider를 조작 중인지 추적하는 state
    var isUserSeeking by remember { mutableStateOf(false) }

    val uiState by viewModel.playerState.collectAsState()

    // 1. 변수 이름을 playerState로 명확하게 변경합니다.
    val playerState by viewModel.playerState.collectAsState()

    // 2. when 표현식을 사용하여 상태별로 UI를 분기합니다.
    when (val state = playerState) {
        // PlayerState가 Idle일 때는 아무것도 표시하지 않습니다.
        is PlayerState.Idle -> {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                //TODO 필요하다면 "재생할 음악을 선택하세요" 같은 플레이스홀더 UI를 여기에 추가?.

            }
        }
        // PlayerState가 Playing 또는 Stopped일 때
        is LoadedState -> {
            PlayerContent(
                viewModel = viewModel,
                state = state // 현재 상태(Playing 또는 Stopped)를 전달
            )
        }
        else -> {}
    }
}

/**
 * 음악이 로드된 상태(재생 또는 정지)일 때의 UI를 담당하는 컴포저블
 */
@OptIn(UnstableApi::class)
@Composable
private fun PlayerContent(
    viewModel: PlayerViewModel,
    state: LoadedState // LoadedState 타입으로 받아서 공통 속성에 접근
) {
    var showControls by remember { mutableStateOf(true) }
    var sliderPosition by remember { mutableStateOf(0f) }
    var isUserSeeking by remember { mutableStateOf(false) }

    // 3. LaunchedEffect의 key를 state의 currentPosition으로 변경합니다.
    LaunchedEffect(state.currentPosition) {
        if (!isUserSeeking) {
            sliderPosition = state.currentPosition.toFloat()
        }
    }

    // 4. Lifecycle 관련 코드는 PlayerContent 내부 또는 PlayerScreen에 있어도 괜찮습니다.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.onPause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().height(300.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // PlayerSurface는 그대로 둡니다.
            PlayerSurface(
                viewModel.player,
                surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
                modifier = Modifier.noRippleClickable { showControls = !showControls },
            )
            // 5. uiState 대신 전달받은 state의 속성을 사용합니다.
            state.albumArt?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "${state.musicResource.originalName} 앨범 아트",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Slider(
                    value = sliderPosition, // 로컬 상태 사용
                    onValueChange = { newPosition ->
                        isUserSeeking = true
                        sliderPosition = newPosition
                    },
                    onValueChangeFinished = {
                        viewModel.seekTo(sliderPosition.toLong())
                        isUserSeeking = false
                    },
                    // 5. uiState 대신 전달받은 state의 속성을 사용합니다.
                    valueRange = 0f..state.duration.toFloat().coerceAtLeast(0f),
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
                    Text(text = formatDuration(sliderPosition.toLong()))
                    Text(text = formatDuration(state.duration))
                }
            }

            if (showControls) {
                ExtraControls(
                    viewModel.player,
                    Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Gray.copy(alpha = 0.4f))
                        .navigationBarsPadding(),
                )
            }
        }
    }
}

// 시간을 mm:ss 형태로 변환해주는 헬퍼 함수
private fun formatDuration(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
