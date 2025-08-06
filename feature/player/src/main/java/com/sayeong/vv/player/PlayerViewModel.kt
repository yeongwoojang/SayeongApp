package com.sayeong.vv.player

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// UI가 관찰할 플레이어의 상태를 담는 데이터 클래스
data class PlayerUiState(
    val isPlaying: Boolean = false,
    // TODO: 현재 재생 시간, 전체 길이, 미디어 정보 등 추가
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: Player
): ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        player.addListener(object: Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                // UI 업데이트
            }
        })

        preparePlayer()

    }

    private fun preparePlayer() {
        val mediaItem = MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    private fun playOrPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}





















