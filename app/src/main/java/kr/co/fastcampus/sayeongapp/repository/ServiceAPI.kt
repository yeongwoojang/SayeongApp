package kr.co.fastcampus.sayeongapp.repository

import kr.co.fastcampus.sayeongapp.dto.Demo
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceAPI {

    @GET("/")
    suspend fun getDemoById(
        @Query("id") id: String
    ): Demo
}