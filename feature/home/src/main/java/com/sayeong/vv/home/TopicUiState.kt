package com.sayeong.vv.home

import com.sayeong.vv.home.model.FileUiModel
import com.sayeong.vv.model.FileResource
import com.sayeong.vv.model.TopicResource

sealed interface TopicUiState {
    data object Loading: TopicUiState
    data class Error(val message: String?): TopicUiState
    data object NotShown: TopicUiState
    data class Shown(
        val topics: List<TopicResource>,
        val selectedTopics: Set<String> = emptySet(),
        //_ 컨텐츠 로딩 중 발생한 에러 메시지
        val contentError: String? = null,
        //_ 최종적으로 불러온 컨텐츠 결과 목록 (임시로 String 타입으로 가정)
        val files: List<FileUiModel> = emptyList(),
        val bookmarkedMusics:Set<FileResource> = emptySet()
    ): TopicUiState
}