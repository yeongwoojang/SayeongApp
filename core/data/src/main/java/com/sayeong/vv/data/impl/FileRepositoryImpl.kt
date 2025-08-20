package com.sayeong.vv.data.impl

import com.sayeong.vv.data.model.toDomainData
import com.sayeong.vv.domain.FileRepository
import com.sayeong.vv.model.FileResource
import com.sayeong.vv.network.api.SayeongApiService
import com.sayeong.vv.network.model.NetworkFileRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val apiService: SayeongApiService
): FileRepository {
    override suspend fun getFileList(): Flow<List<FileResource>> = flow {
        emit(apiService.getFileList().map { it.toDomainData() })
    }

    override suspend fun getFileListByGenre(genres: List<String>): Flow<List<FileResource>> = flow {
        emit(apiService.getFileListByGenre(NetworkFileRequest(genres)).map { it.toDomainData() })
    }
}