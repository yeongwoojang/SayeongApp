package com.sayeong.vv.domain

import com.sayeong.vv.model.MusicResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkedMusicUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(): Flow<List<MusicResource>> {
        return musicRepository.getBookmarkedMusic()
    }
}