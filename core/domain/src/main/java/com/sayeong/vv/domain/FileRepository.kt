package com.sayeong.vv.domain

import com.sayeong.vv.model.FileResource

interface FileRepository {
    suspend fun getFileList(): List<FileResource>
}