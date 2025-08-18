package com.sayeong.vv.data.impl

import com.sayeong.vv.data.model.toDomainData
import com.sayeong.vv.domain.TopicRepository
import com.sayeong.vv.model.TopicResource
import com.sayeong.vv.network.api.SayeongApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val apiService: SayeongApiService
): TopicRepository {
    override suspend fun getTopics(): Flow<List<TopicResource>> = flow{
        emit(apiService.getTopics().map { it.toDomainData() })
    }
}