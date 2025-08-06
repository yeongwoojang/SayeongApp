package com.sayeong.vv.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()


    // 5. UI 레이아웃
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 기존의 Android View인 PlayerView를 Jetpack Compose에서 사용하기 위해 AndroidView를 사용합니다.
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f), // 오디오이므로 화면 비율은 중요하지 않지만, 컨트롤러 공간 확보를 위해 설정
            factory = {
                // 이 블록은 Composable이 처음 생성될 때 한 번만 호출됩니다.
                PlayerView(it).apply {
                    player = viewModel.player
                    useController = true // 기본 컨트롤러 UI(재생/정지 버튼, 탐색 바 등)를 사용합니다.
                }
            }
        )

        Text("ExoPlayer가 여기에 표시됩니다!")
    }
}

/**
 * 플레이어의 생명주기를 관찰하고, 화면이 보이지 않을 때(ON_PAUSE) 재생을 멈추는 역할을 합니다.
 * @param player 제어할 ExoPlayer 인스턴스
 */
@Composable
private fun PlayerLifecycleObserver(player: ExoPlayer) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                player.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
















