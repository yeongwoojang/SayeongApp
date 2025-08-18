package com.sayeong.vv.network.api

import com.sayeong.vv.network.model.NetworkFile
import com.sayeong.vv.network.model.NetworkTopic
import retrofit2.http.GET

interface SayeongApiService {
    @GET("files")
    suspend fun getFileList(): List<NetworkFile>

    @GET("topics")
    suspend fun getTopics(): List<NetworkTopic>

}