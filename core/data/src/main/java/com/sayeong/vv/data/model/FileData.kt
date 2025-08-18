package com.sayeong.vv.data.model

import com.sayeong.vv.model.FileResource
import com.sayeong.vv.network.model.NetworkFile

fun NetworkFile.toDomainData() = FileResource(
    id = id,
    originalName = originalName,
    storedFileName = storedFileName,
    fileSize = fileSize,
    duration = duration,
    artist = artist,
    genre = genre,
    createdAt = createdAt,
    updatedAt = updatedAt
)
