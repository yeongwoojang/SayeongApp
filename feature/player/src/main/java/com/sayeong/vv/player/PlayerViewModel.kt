package com.sayeong.vv.player

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.sayeong.vv.domain.GetAlbumArtUseCase
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.player.model.LoadedState
import com.sayeong.vv.player.model.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: Player,
    private val getAlbumArtUseCase: GetAlbumArtUseCase
): ViewModel() {

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val playerState =_playerState.asStateFlow()


    private val availableSpeeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

    // 재생 시간 업데이트를 위한 Flow
    val playerPosition = flow {
        while (true) {
            if (player.isPlaying) {
                emit(player.currentPosition)
            }
            delay(1000) // 1초마다 업데이트
        }
    }


    init {
        player.addListener(object: Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // UI 업데이트
                Timber.i("onIsPlayingChanged() | isPlaying: $isPlaying")
                val currentState = _playerState.value as LoadedState
                if (isPlaying) {
                    _playerState.value = PlayerState.Playing(
                        musicResource = currentState.musicResource,
                        albumArt = currentState.albumArt,
                        duration = player.duration,
                        currentPosition = currentState.currentPosition,
                        playbackSpeed = currentState.playbackSpeed,
                    )
                } else {
                    _playerState.value = PlayerState.Stopped(
                        musicResource = currentState.musicResource,
                        albumArt = currentState.albumArt,
                        duration = player.duration,
                        currentPosition = currentState.currentPosition,
                        playbackSpeed = currentState.playbackSpeed,
                    )
                }
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                Timber.i("onPlaybackParametersChanged() | playbackParameters: ${playbackParameters.speed}")
                if (_playerState.value is PlayerState.Playing) {
                    _playerState.update { (it as PlayerState.Playing).copy(playbackSpeed = playbackParameters.speed) }
                } else if (_playerState.value is PlayerState.Stopped) {
                    _playerState.update { (it as PlayerState.Stopped).copy(playbackSpeed = playbackParameters.speed) }
                }
            }
        })

        viewModelScope.launch {
            playerPosition.collect { position ->
                if (_playerState.value is PlayerState.Playing) {
                    _playerState.update { (it as PlayerState.Playing).copy(currentPosition = position) }
                } else if (_playerState.value is PlayerState.Stopped) {
                    _playerState.update { (it as PlayerState.Stopped).copy(currentPosition = position) }
                }
            }
        }
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
        if (_playerState.value is PlayerState.Playing) {
            _playerState.update { (it as PlayerState.Playing).copy(currentPosition = position) }
        }
    }

    fun playMusic(music: MusicResource) {
        if (_playerState.value != PlayerState.Idle) {
            val prevMusic = _playerState.value as LoadedState
            if (music == prevMusic.musicResource) {
                return
            }
        }

        _playerState.value = PlayerState.Playing(
            musicResource = music,
            currentPosition = 0L,
        )
        val currentState = _playerState.value
        viewModelScope.launch {
            _playerState.update {
                (currentState as PlayerState.Playing).copy(
                    albumArt = getBitMap(music.originalName)
                )
            }
        }

        val mediaItem = MediaItem.fromUri("http://10.0.2.2:3000/uploads/${music.originalName}")
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private suspend fun getBitMap(resourceName: String): Bitmap? {
        val albumArtByte = getAlbumArtUseCase(resourceName)
        val bitmap = if (albumArtByte != null) {
            BitmapFactory.decodeByteArray(albumArtByte, 0, albumArtByte.size)
        } else {
            null
        }
        return bitmap
    }

    fun onPause() {
        Timber.i("onPause()")
        player.pause()
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





















