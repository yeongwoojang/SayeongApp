package kr.co.fastcampus.sayeongapp.domain

import kr.co.fastcampus.sayeongapp.dto.Demo

interface DemoRepository {
    suspend fun getDemoById(id: String): Result<Demo>
}