package com.sayeong.vv.domain

import com.sayeong.vv.model.TopicResource
import kotlinx.coroutines.flow.Flow

interface TopicRepository {
    fun getTopics(): Flow<List<TopicResource>>
}