package com.sayeong.vv.model

data class MusicResource(
    val id: Int,
    val originalName: String,
    val storedFileName: String,
    val fileSize: Long,
    val duration: Int?,
    val artist: String?,
    val genre: String?,
)
