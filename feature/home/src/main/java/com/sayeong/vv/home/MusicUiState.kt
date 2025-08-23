package com.sayeong.vv.home

import com.sayeong.vv.home.model.MusicUiModel
import com.sayeong.vv.model.MusicResource

sealed interface MusicUiState {
    data class Error(val message: String?): MusicUiState
    data class Shown(
        val files: List<MusicUiModel> = emptyList(),
        val bookmarkedMusics:Set<MusicResource> = emptySet()
    ): MusicUiState

}