package com.sayeong.vv.network.api

import com.sayeong.vv.network.model.NetworkFile
import retrofit2.http.GET

interface SayeongApiService {
    @GET("files")
    suspend fun getFileList(): List<NetworkFile>

}