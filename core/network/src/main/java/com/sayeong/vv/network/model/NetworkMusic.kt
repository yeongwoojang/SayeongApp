package com.sayeong.vv.network.model

data class NetworkMusic(
    val id: Int,
    val originalName: String = "",
    val storedFileName: String = "",
    val fileSize: Long,
    val duration: Int?,
    val artist: String = "",
    val genre: String = "",
)
