package com.sayeong.vv.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_music")
data class BookmarkedMusicEntity(
    @PrimaryKey
    val id: Int,
    val originalName: String,
    val storedFileName: String,
    val fileSize: Long,
    val duration: Int?,
    val artist: String?,
    val genre: String?,
)
