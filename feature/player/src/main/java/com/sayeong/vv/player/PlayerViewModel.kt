package com.sayeong.vv.player

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.sayeong.vv.domain.GetFileListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

// UI가 관찰할 플레이어의 상태를 담는 데이터 클래스
data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isSilenceSkippingEnabled: Boolean = false, //_ '무음 건너뛰기' 기능의 활성화 상태를 UI에 알려주기 위한 변수
    val playbackSpeed: Float = 1.0f
    // TODO: 현재 재생 시간, 전체 길이, 미디어 정보 등 추가
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: Player,
    private val silenceSkippingAudioProcessor: SilenceSkippingAudioProcessor,
    private val getFileListUseCase: GetFileListUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    private val availableSpeeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

    init {
        //_ viewModel이 생성될 때, AudioPRocessor의 현재 상태를 UI State에 전달
        _uiState.update { it.copy(isSilenceSkippingEnabled = silenceSkippingAudioProcessor.isEnabled) }

        player.addListener(object: Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // UI 업데이트
                Timber.i("onIsPlayingChanged() | isPlaying: $isPlaying")
                _uiState.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                _uiState.update { it.copy(playbackSpeed = playbackParameters.speed) }
            }
        })

        preparePlayer()

    }

    private fun preparePlayer() {
        val mediaItem = MediaItem.fromUri("http://10.0.2.2:3000/uploads/seed-file.mp3")
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun playOrPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun onPause() {
        Timber.i("onPause()")
        player.pause()
    }

    /**
     * '무음 건너뛰기' 기능을 켜고 끄는 함수. UI에서 이 함수를 호출
     */
    fun toggleSilenceSkipping() {
        //_ 현재 상태를 반전시켜 AudioProcessor에 적용합니다.
        val newState = !silenceSkippingAudioProcessor.isEnabled
        Timber.i("toggleSilenceSkipping() | newState: $newState")
        silenceSkippingAudioProcessor.isEnabled = newState
        // 변경된 상태를 UI State에 반영합니다.
        _uiState.update { it.copy(isSilenceSkippingEnabled = newState) }
    }

    /**
     * 재생 속도를 다음 단계로 변경하는 함수. UI에서 호출됩니다.
     */
    fun changePlaybackSpeed() {
        val currentSpeed = player.playbackParameters.speed
        val currentIndex = availableSpeeds.indexOf(currentSpeed)
        val nextIndex = (currentIndex + 1) % availableSpeeds.size
        val newSpeed = availableSpeeds[nextIndex]

        // 새로운 속도를 가진 PlaybackParameters 객체를 생성하여 플레이어에 설정합니다.
        // pitch는 1.0f로 유지하여 음정 변화가 없도록 합니다.
        player.playbackParameters = PlaybackParameters(newSpeed)
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared()")
        player.release()
    }
}





















