package com.sayeong.vv.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import timber.log.Timber

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f), //_ 오디오이므로 화면 비율은 중요하지 않지만, 컨트롤러 공간 확보를 위해 설정
            factory = { context ->
                val playerView = PlayerView(context).apply {
                    player = viewModel.player
                    useController = true // 기본 컨트롤러 UI(재생/정지 버튼, 탐색 바 등)를 사용합니다.
                    setShowFastForwardButton(true)
                }

                val overlayView = LayoutInflater.from(context).inflate(
                    R.layout.custom_player_overlay,
                    playerView,
                    false
                )

                val bottomBar = playerView.findViewById<ViewGroup?>(androidx.media3.ui.R.id.exo_bottom_bar)
                bottomBar?.apply {
                    val basicControls = findViewById<ViewGroup>(androidx.media3.ui.R.id.exo_basic_controls);
                    val settingsButton = findViewById<View?>(androidx.media3.ui.R.id.exo_settings)

                    if (basicControls != null && settingsButton != null) {
                        val settingsButtonIndex = basicControls.indexOfChild(settingsButton)
                        if (settingsButtonIndex != -1) {
                            basicControls.addView(overlayView, settingsButtonIndex)
                        } else {
                            basicControls.addView(overlayView)

                        }
                    } else {
                        (playerView as ViewGroup).addView(overlayView)
                    }
                }

                overlayView.findViewById<ImageButton>(R.id.playback_speed_button)
                    .setOnClickListener { viewModel.changePlaybackSpeed() }
                overlayView.findViewById<TextView>(R.id.skip_silence_toggle)
                    .setOnClickListener { viewModel.toggleSilenceSkipping() }
                playerView
            },
            update = { playerView ->
                // 4. uiState가 변경될 때마다 버튼의 아이콘을 업데이트합니다.
                val speedChangeButton = playerView.findViewById<TextView>(R.id.fast_text)
                speedChangeButton.text = "${uiState.playbackSpeed}x"
                val skipSilenceButton = playerView.findViewById<TextView>(R.id.skip_silence_toggle)
                skipSilenceButton.text = if (uiState.isSilenceSkippingEnabled) {
                    "skip Silence off"
                } else {
                    "skip Silence on"
                }
            }
        )
    }
}