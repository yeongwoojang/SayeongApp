package com.sayeong.vv.domain

import com.sayeong.vv.model.MusicResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMusicByGenreUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(genres: List<String>): Flow<List<MusicResource>> {
        return fileRepository.getMusicListByGenre(genres)
    }

}