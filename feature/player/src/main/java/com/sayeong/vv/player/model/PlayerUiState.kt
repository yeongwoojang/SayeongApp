package com.sayeong.vv.player.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import com.sayeong.vv.model.MusicResource

interface LoadedState {
    val musicResource: MusicResource
    val albumArt: Bitmap?
    val duration: Long
    val currentPosition: Long
    val playbackSpeed: Float
    val dominantColor: Color?
    val gradientColor: Color?
}

sealed interface PlayerState {
    data object Idle: PlayerState

    data class Stopped(
        override val musicResource: MusicResource,
        override val albumArt: Bitmap? = null,
        override val duration: Long = 0L,
        override val currentPosition: Long = 0L,
        override val playbackSpeed: Float = 0f,
        override val dominantColor: Color? = null,
        override val gradientColor: Color? = null
    ): LoadedState, PlayerState

    data class Playing(
        override val musicResource: MusicResource,
        override val albumArt: Bitmap? = null,
        override val duration: Long = 0L,
        override val currentPosition: Long = 0L,
        override val playbackSpeed: Float = 0f,
        override val dominantColor: Color? = null,
        override val gradientColor: Color? = null
    ) : PlayerState, LoadedState // LoadedState 인터페이스 구현
}