package com.sayeong.vv.domain

import com.sayeong.vv.model.TopicResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopicsUseCase @Inject constructor(
    private val topicRepository: TopicRepository
) {
    suspend operator fun invoke(): Flow<List<TopicResource>> {
        return topicRepository.getTopics()
    }
}