package com.sayeong.vv.home

import com.sayeong.vv.home.model.FileUiModel
import com.sayeong.vv.model.FileResource
import com.sayeong.vv.model.TopicResource

sealed interface TopicUiState {
    data object Loading: TopicUiState
    data class Error(val message: String?): TopicUiState
    data class NotShown(
        val topics: List<TopicResource> = emptyList(),
        val selectedTopics: Set<String> = emptySet(),
    ): TopicUiState
    data class Shown(
        val topics: List<TopicResource>,
        val selectedTopics: Set<String> = emptySet(),
    ): TopicUiState
}