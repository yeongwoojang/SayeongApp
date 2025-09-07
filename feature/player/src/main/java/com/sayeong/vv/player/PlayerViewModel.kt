package com.sayeong.vv.player

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.palette.graphics.Palette
import com.sayeong.vv.domain.GetAlbumArtUseCase
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.player.model.LoadedState
import com.sayeong.vv.player.model.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

private data class AlbumArtResult(val bitmap: Bitmap, val domainColor: Color, val gradientColor: Color)


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
                _playerState.update { currentState ->
                    if (currentState is LoadedState) {
                        if (isPlaying) {
                            PlayerState.Playing(
                                musicResource = currentState.musicResource,
                                albumArt = currentState.albumArt,
                                duration = player.duration,
                                currentPosition = currentState.currentPosition,
                                playbackSpeed = currentState.playbackSpeed,
                                dominantColor = currentState.dominantColor,
                                gradientColor = currentState.gradientColor
                            )
                        } else {
                            PlayerState.Stopped(
                                musicResource = currentState.musicResource,
                                albumArt = currentState.albumArt,
                                duration = player.duration,
                                currentPosition = currentState.currentPosition,
                                playbackSpeed = currentState.playbackSpeed,
                                dominantColor = currentState.dominantColor,
                                gradientColor = currentState.gradientColor
                            )
                        }
                    } else {
                        currentState
                    }
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

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val newDuration = player.duration
                    // duration이 유효한 경우에만 상태를 업데이트합니다.
                    if (newDuration > 0) {
                        _playerState.update { currentState ->
                            // 현재 상태가 LoadedState일 때만 duration을 갱신합니다.
                            if (currentState is LoadedState) {
                                when(currentState) {
                                    is PlayerState.Playing -> currentState.copy(duration = newDuration)
                                    is PlayerState.Stopped -> currentState.copy(duration = newDuration)
                                    PlayerState.Idle -> currentState
                                }
                            } else {
                                currentState
                            }
                        }
                    }
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

        Timber.i("TEST_LOG | playMusic")
        _playerState.value = PlayerState.Playing(
            musicResource = music,
            currentPosition = 0L,
        )
        viewModelScope.launch {
            val albumArtResult = getAlbumArtAndColor(music.originalName)
            _playerState.update {
                (it as PlayerState.Playing).copy(
                    albumArt = albumArtResult?.bitmap,
                    dominantColor = albumArtResult?.domainColor,
                    gradientColor = albumArtResult?.gradientColor
                )
            }
        }

        val mediaItem = MediaItem.fromUri("http://10.0.2.2:3000/uploads/${music.originalName}")
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
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


    private suspend fun getAlbumArtAndColor(resourceName: String): AlbumArtResult? {
        val albumArtByte = getAlbumArtUseCase(resourceName) ?: return null

        return withContext(Dispatchers.Default) {
            val bitmap = BitmapFactory.decodeByteArray(albumArtByte, 0, albumArtByte.size)
            val palette = Palette.from(bitmap).generate()

            // 대표 색상은 기존과 같이 어두운 Vibrant 색상으로 선택
            val dominantColor = palette.darkVibrantSwatch?.rgb?.let { Color(it) }
                ?: palette.darkMutedSwatch?.rgb?.let { Color(it) }
                ?: Color.Gray

            // 그라데이션 색상은 Muted 색상 계열로 선택하여 부드러운 조화를 유도
            val gradientColor = palette.mutedSwatch?.rgb?.let { Color(it) }
                ?: palette.lightMutedSwatch?.rgb?.let { Color(it) }
                ?: dominantColor // 마땅한 색이 없으면 대표 색상과 동일하게 설정

            AlbumArtResult(bitmap, dominantColor, gradientColor)
        }
    }
}
