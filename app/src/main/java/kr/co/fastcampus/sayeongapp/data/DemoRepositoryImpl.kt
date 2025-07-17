package kr.co.fastcampus.sayeongapp.data

import android.util.Log
import kr.co.fastcampus.sayeongapp.domain.DemoRepository
import kr.co.fastcampus.sayeongapp.dto.Demo
import kr.co.fastcampus.sayeongapp.repository.ServiceAPI
import javax.inject.Inject


class DemoRepositoryImpl @Inject constructor(
    private val serviceAPI: ServiceAPI
): DemoRepository {
    override suspend fun getDemoById(id: String): Result<Demo> = kotlin.runCatching {
        serviceAPI.getDemoById(id)
    }.onFailure {
        Log.d("TEST_LOG", "에러 발생!")
    }
}