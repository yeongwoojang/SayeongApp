package com.sayeong.vv.data.impl

import com.sayeong.vv.data.model.toDomainData
import com.sayeong.vv.data.model.toEntity
import com.sayeong.vv.database.BookmarkedMusicDao
import com.sayeong.vv.domain.MusicRepository
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.network.api.SayeongApiService
import com.sayeong.vv.network.model.NetworkFileRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val apiService: SayeongApiService,
    private val bookmarkedMusicDao: BookmarkedMusicDao
): MusicRepository {
    override fun getMusicList(): Flow<List<MusicResource>> = flow {
        emit(apiService.getMusicList().map { it.toDomainData() })
    }

    override fun getMusicListByGenre(genres: List<String>): Flow<List<MusicResource>> = flow {
        emit(apiService.getMusicListByGenre(NetworkFileRequest(genres)).map { it.toDomainData() })
    }

    override fun getMusicBySearch(query: String): Flow<List<MusicResource>> = flow {
        emit(apiService.searchMusic(query).map { it.toDomainData() })
    }

    override fun getBookmarkedMusic(): Flow<List<MusicResource>> = bookmarkedMusicDao.getAllBookmarkedMusics().map {
        it.map { entity -> entity.toDomainData() }
    }

    override suspend fun addBookmark(musicResource: MusicResource) {
        bookmarkedMusicDao.insert(musicResource.toEntity())
    }

    override suspend fun removeBookmark(musicResource: MusicResource) {
        bookmarkedMusicDao.delete(musicResource.toEntity())
    }
}