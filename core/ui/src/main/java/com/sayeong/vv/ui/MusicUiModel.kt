package com.sayeong.vv.ui

import android.graphics.Bitmap
import com.sayeong.vv.model.MusicResource

data class MusicUiModel(
    val musicResource: MusicResource,
    val albumArt: Bitmap? = null,
    val isArtLoading: Boolean = false,
) {
    val id: Int get() = musicResource.id
}