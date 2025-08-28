package com.sayeong.vv.home

import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.ui.MusicUiModel

sealed interface MusicUiState {
    data class Error(val message: String?): MusicUiState
    data class Shown(
        val musics: List<MusicUiModel> = emptyList(),
        val bookmarkedMusics:Set<MusicResource> = emptySet()
    ): MusicUiState

}