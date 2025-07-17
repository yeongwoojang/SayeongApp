package kr.co.fastcampus.sayeongapp.domain

import kr.co.fastcampus.sayeongapp.dto.Demo
import javax.inject.Inject

class DemoUseCase @Inject constructor(
    private val demoRepository: DemoRepository
) {
    suspend operator fun invoke(id: String): Result<Demo> {
        val result = demoRepository.getDemoById(id)
        return result
    }
}