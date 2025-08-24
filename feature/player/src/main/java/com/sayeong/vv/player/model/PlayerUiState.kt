package com.sayeong.vv.player.model

import android.graphics.Bitmap
import com.sayeong.vv.model.MusicResource

interface LoadedState {
    val musicResource: MusicResource
    val albumArt: Bitmap?
    val duration: Long
    val currentPosition: Long
    val playbackSpeed: Float
}

sealed interface PlayerState {
    data object Idle: PlayerState

    data class Stopped(
        override val musicResource: MusicResource,
        override val albumArt: Bitmap? = null,
        override val duration: Long = 0L,
        override val currentPosition: Long = 0L,
        override val playbackSpeed: Float = 0f
    ): LoadedState, PlayerState

    data class Playing(
        override val musicResource: MusicResource,
        override val albumArt: Bitmap? = null,
        override val duration: Long = 0L,
        override val currentPosition: Long = 0L,
        override val playbackSpeed: Float = 0f
    ) : PlayerState, LoadedState // LoadedState 인터페이스 구현
}