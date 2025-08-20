package com.sayeong.vv.network.api

import com.sayeong.vv.network.model.NetworkFile
import com.sayeong.vv.network.model.NetworkFileRequest
import com.sayeong.vv.network.model.NetworkTopic
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SayeongApiService {
    @GET("files")
    suspend fun getFileList(): List<NetworkFile>

    @GET("topics")
    suspend fun getTopics(): List<NetworkTopic>

    @POST("files/by-genre")
    suspend fun getFileListByGenre(
        @Body requestBody: NetworkFileRequest
    ): List<NetworkFile>
}