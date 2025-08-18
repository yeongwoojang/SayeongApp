package com.sayeong.vv.data.model

import com.sayeong.vv.model.TopicResource
import com.sayeong.vv.network.model.NetworkTopic

fun NetworkTopic.toDomainData() = TopicResource(
    id = this.id,
    title = this.title,
    imageUrl = this.imageUrl
)