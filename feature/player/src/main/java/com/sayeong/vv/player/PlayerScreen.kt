package com.sayeong.vv.player

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
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
                player = viewModel.player,
                onPause = viewModel::onPause,
                onSeekTo = viewModel::seekTo,
                state = state // 현재 상태(Playing 또는 Stopped)를 전달
            )
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

    LaunchedEffect(state.currentPosition) {
        if (!isUserSeeking) {
            sliderPosition = state.currentPosition.toFloat()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                onPause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // --- 이 부분을 새로운 Column 구조로 변경합니다 ---
    Column(
        modifier = Modifier
            .fillMaxSize() // Column이 전체를 채우도록 변경
            .background(MaterialTheme.colorScheme.surface) // 배경색 지정
    ) {
        // 1. 앨범 아트 섹션 (화면의 남는 공간을 모두 차지)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // 남는 공간을 모두 차지
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
                )
            }
        }

        // 2. 음악 정보 및 컨트롤러 섹션
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // 하단 네비게이션 바 영역 확보
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 음악 제목
            Text(
                text = state.musicResource.originalName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            // 아티스트 이름 (예시)
            Text(
                text = state.musicResource.artist ?: "Unknown Artist",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            // 플레이어 바 (슬라이더, 시간)
            PlayerBar(
                modifier = Modifier.padding(top = 16.dp),
                sliderPosition = sliderPosition,
                onValueChange = { newPosition ->
                    isUserSeeking = true // isUserSeeking 상태 업데이트 추가
                    sliderPosition = newPosition
                },
                onValueChangeFinished = {
                    onSeekTo(sliderPosition.toLong())
                    isUserSeeking = false
                },
                duration = state.duration
            )

            // 재생/일시정지, 이전/다음 등 추가 컨트롤
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

// 시간을 mm:ss 형태로 변환해주는 헬퍼 함수
private fun formatDuration(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
