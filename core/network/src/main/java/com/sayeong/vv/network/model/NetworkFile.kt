package com.sayeong.vv.network.model

import com.google.gson.annotations.SerializedName

data class NetworkFile(
    val id: Int,
    val originalName: String = "",
    val storedFileName: String = "",
    val fileSize: Long,
    val duration: Int?,
    val artist: String = "",
    val genre: String = "",
)
