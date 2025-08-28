package com.sayeong.vv.search

import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.ui.MusicUiModel

sealed interface SearchUiState {
    data object Loading: SearchUiState
    data class Error(
        val message: String? = null,
    ): SearchUiState
    data class Shown(
        val musicResources: List<MusicUiModel> = emptyList(),
        val bookmarkedMusics:Set<MusicResource> = emptySet()
    ): SearchUiState
}