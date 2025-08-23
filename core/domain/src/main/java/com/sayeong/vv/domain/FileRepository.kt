package com.sayeong.vv.domain

import com.sayeong.vv.model.MusicResource
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    suspend fun getMusicList(): Flow<List<MusicResource>>
    suspend fun getMusicListByGenre(genres: List<String>): Flow<List<MusicResource>>
}