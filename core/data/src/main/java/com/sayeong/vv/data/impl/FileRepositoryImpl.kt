package com.sayeong.vv.data.impl

import com.sayeong.vv.data.model.toDomainData
import com.sayeong.vv.domain.FileRepository
import com.sayeong.vv.model.FileResource
import com.sayeong.vv.network.api.SayeongApiService
import timber.log.Timber
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val apiService: SayeongApiService
): FileRepository {
    override suspend fun getFileList(): List<FileResource> {
        //TODO 파일 리스트 받아와서 결과 값 반환 로직 작성

        val result = apiService.getFileList().map {
            it.toDomainData()
        }
        Timber.i("getFileList() | result: $result")
        return result
    }
}