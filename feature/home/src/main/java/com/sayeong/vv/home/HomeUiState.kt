package com.sayeong.vv.home

import com.sayeong.vv.home.model.FileUiModel
import com.sayeong.vv.model.TopicResource

sealed interface HomeUiState {
    data object Loading: HomeUiState
    data class Error(val message: String?): HomeUiState
    data object NotShown: HomeUiState
    data class Shown(
        val topics: List<TopicResource>,
        val selectedTopics: Set<String> = emptySet(),
        //_ Done 버튼 클릭 후 컨텐츠를 로딩 중인지 여부
        val isFileListLoading: Boolean = false,
        //_ 컨텐츠 로딩 중 발생한 에러 메시지
        val contentError: String? = null,
        //_ 최종적으로 불러온 컨텐츠 결과 목록 (임시로 String 타입으로 가정)
        val files: List<FileUiModel> = emptyList(),
    ): HomeUiState
}