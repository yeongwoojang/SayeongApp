package com.sayeong.vv.network.model

import com.google.gson.annotations.SerializedName

data class NetworkFile(
    @SerializedName("id")
    val id: Int,
    @SerializedName("originalName")
    val originalName: String,
    @SerializedName("storedFileName")
    val storedFileName: String,
    @SerializedName("fileSize")
    val fileSize: Long,
    @SerializedName("duration")
    val duration: Int?,
    @SerializedName("artist")
    val artist: String?,
    @SerializedName("genre")
    val genre: String?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)
