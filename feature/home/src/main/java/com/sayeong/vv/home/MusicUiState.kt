package com.sayeong.vv.home

import com.sayeong.vv.home.model.FileUiModel
import com.sayeong.vv.model.FileResource

sealed interface MusicUiState {
    data class Error(val message: String?): MusicUiState
    data class Shown(
        val files: List<FileUiModel> = emptyList(),
        val bookmarkedMusics:Set<FileResource> = emptySet()
    ): MusicUiState

}