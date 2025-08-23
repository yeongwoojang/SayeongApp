package com.sayeong.vv.data.model

import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.network.model.NetworkMusic

fun NetworkMusic.toDomainData() = MusicResource(
    id = this.id,
    originalName = this.originalName,
    storedFileName = this.storedFileName,
    fileSize = this.fileSize,
    duration = this.duration,
    artist = this.artist,
    genre = this.genre,
)
