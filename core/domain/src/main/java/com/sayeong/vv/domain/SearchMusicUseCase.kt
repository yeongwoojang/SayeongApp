package com.sayeong.vv.domain

import com.sayeong.vv.model.MusicResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMusicUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(query: String): Flow<List<MusicResource>> {
        return musicRepository.getMusicBySearch(query)
    }
}