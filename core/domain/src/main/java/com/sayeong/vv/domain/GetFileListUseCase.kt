package com.sayeong.vv.domain

import com.sayeong.vv.model.FileResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFileListUseCase @Inject constructor(
    private val repository: FileRepository
){
    suspend operator fun invoke(): Flow<List<FileResource>> {
        return repository.getFileList()
    }
}