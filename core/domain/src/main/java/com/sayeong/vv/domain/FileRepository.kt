package com.sayeong.vv.domain

import com.sayeong.vv.model.FileResource
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    suspend fun getFileList(): Flow<List<FileResource>>
    suspend fun getFileListByGenre(genres: List<String>): Flow<List<FileResource>>
}