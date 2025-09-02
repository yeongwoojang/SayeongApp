package com.sayeong.vv.domain

import com.sayeong.vv.model.MusicResource
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getMusicList(): Flow<List<MusicResource>>
    fun getMusicListByGenre(genres: List<String>): Flow<List<MusicResource>>
    fun getMusicBySearch(query: String): Flow<List<MusicResource>>
    fun getBookmarkedMusic(): Flow<List<MusicResource>>
    suspend fun addBookmark(musicResource: MusicResource)
    suspend fun removeBookmark(musicResource: MusicResource)
}