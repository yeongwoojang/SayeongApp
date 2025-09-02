package com.sayeong.vv.domain

import com.sayeong.vv.model.MusicResource
import javax.inject.Inject

class InsertBookmarkedUseCase @Inject constructor(
    private val musicRepository: MusicRepository
){
    suspend operator fun invoke(musicResource: MusicResource) {
        musicRepository.addBookmark(musicResource)
    }

}