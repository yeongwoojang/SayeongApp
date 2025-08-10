package com.sayeong.vv.player

import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.ui.PlayerView
import timber.log.Timber

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

//    PlayerLifecycleObserver(viewModel.player)
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
                val playerView = PlayerView(it).apply {
                    player = viewModel.player
                    useController = true // 기본 컨트롤러 UI(재생/정지 버튼, 탐색 바 등)를 사용합니다.
                }

                // 2. 우리가 만든 커스텀 버튼 레이아웃을 가져와서(inflate) PlayerView에 추가합니다.
                // PlayerView는 FrameLayout을 상속받으므로, 자식 뷰를 추가할 수 있습니다.
                LayoutInflater.from(it).inflate(
                    R.layout.custom_skip_silence_button,
                    playerView, // playerView를 부모로 지정
                    true      // 즉시 추가
                )

                // 3. 추가된 버튼을 찾아서 클릭 리스너를 설정합니다.
                playerView.findViewById<ImageButton>(R.id.skip_silence_toggle)
                    .setOnClickListener { viewModel.toggleSilenceSkipping() }

                playerView
            },
            update = { playerView ->
                // 4. uiState가 변경될 때마다 버튼의 아이콘을 업데이트합니다.
                val skipSilenceButton = playerView.findViewById<ImageButton>(R.id.skip_silence_toggle)
                val iconRes = if (uiState.isSilenceSkippingEnabled) {
                    R.drawable.speaker_notes_24px
                } else {
                    R.drawable.speaker_notes_off_24px
                }
                skipSilenceButton.setImageResource(iconRes)
            }
        )
    }
}