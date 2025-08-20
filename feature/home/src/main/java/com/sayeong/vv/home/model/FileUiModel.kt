package com.sayeong.vv.home.model

import android.graphics.Bitmap
import com.sayeong.vv.model.FileResource

data class FileUiModel(
    val fileResource: FileResource,
    val albumArt: Bitmap? = null,
    val isArtLoading: Boolean = false,
) {
    val id: Int get() = fileResource.id
}
