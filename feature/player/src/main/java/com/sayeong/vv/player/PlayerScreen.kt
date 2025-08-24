package com.sayeong.vv.player

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.state.rememberPresentationState
import com.sayeong.vv.player.component.CONTENT_SCALES
import com.sayeong.vv.player.component.ExtraControls
import com.sayeong.vv.player.component.noRippleClickable
import timber.log.Timber
import java.util.concurrent.TimeUnit

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    var showControls by remember { mutableStateOf(true) }
    var sliderPosition by remember { mutableStateOf(0f) }

    // 2. 사용자가 Slider를 조작 중인지 추적하는 상태
    var isUserSeeking by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.currentPosition) {
        if (!isUserSeeking) {
            sliderPosition = uiState.currentPosition.toFloat()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            Timber.i("PlayerLifecycleObserver:: event: $event")
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.onPause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val presentationState = rememberPresentationState(viewModel.player)


    Column(
        modifier = Modifier.fillMaxWidth().height(300.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(Color.Yellow)) {
            PlayerSurface(
                viewModel.player,
                surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
                modifier = Modifier.noRippleClickable { showControls = !showControls },

                )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Slider(
                    value = sliderPosition,
                    onValueChange = { newPosition ->
                        isUserSeeking = true
                        sliderPosition = newPosition

                    },
                    onValueChangeFinished = {
                        // 드래그가 끝나면 ViewModel에 최종 위치 전달
                        viewModel.seekTo(sliderPosition.toLong())
                        // 사용자 조작이 끝났으므로 isUserSeeking을 false로 설정
                        isUserSeeking = false
                    },
                    valueRange = 0f..uiState.duration.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = formatDuration(uiState.currentPosition))
                    Text(text = formatDuration(uiState.duration))
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
