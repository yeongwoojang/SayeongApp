package com.sayeong.vv.domain

import com.sayeong.vv.model.FileResource
import javax.inject.Inject

class GetFileListUseCase @Inject constructor(
    private val repository: FileRepository
){
    suspend operator fun invoke(): List<FileResource> {
        return repository.getFileList()
    }
}