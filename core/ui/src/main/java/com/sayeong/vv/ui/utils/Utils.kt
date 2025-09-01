package com.sayeong.vv.ui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun ByteArray?.toBitmap(): Bitmap? {
    val bitmap = if (this != null) {
        BitmapFactory.decodeByteArray(this, 0, this.size)
    } else {
        null
    }
    return bitmap
}