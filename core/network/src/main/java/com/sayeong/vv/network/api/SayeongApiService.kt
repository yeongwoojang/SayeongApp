package com.sayeong.vv.network.api

import com.sayeong.vv.network.model.NetworkMusic
import com.sayeong.vv.network.model.NetworkFileRequest
import com.sayeong.vv.network.model.NetworkTopic
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SayeongApiService {
    @GET("files")
    suspend fun getMusicList(): List<NetworkMusic>

    @GET("topics")
    suspend fun getTopics(): List<NetworkTopic>

    @POST("files/by-genre")
    suspend fun getMusicListByGenre(
        @Body requestBody: NetworkFileRequest
    ): List<NetworkMusic>

    @GET("search")
    suspend fun searchMusic(
        @Query("q") query: String
    ): List<NetworkMusic>
}