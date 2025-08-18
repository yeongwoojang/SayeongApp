package com.sayeong.vv.data.model

import com.sayeong.vv.model.FileResource
import com.sayeong.vv.network.model.NetworkFile

fun NetworkFile.toDomainData() = FileResource(
    id = this.id,
    originalName = this.originalName,
    storedFileName = this.storedFileName,
    fileSize = this.fileSize,
    duration = this.duration,
    artist = this.artist,
    genre = this.genre,
)
