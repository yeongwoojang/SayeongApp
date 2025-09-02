package com.sayeong.vv.bookmark

import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.ui.MusicUiModel

sealed interface BookmarkUIState {
    data object Loading : BookmarkUIState
    data class Shown(
        val musicResources: List<MusicUiModel>,
    ) : BookmarkUIState
}