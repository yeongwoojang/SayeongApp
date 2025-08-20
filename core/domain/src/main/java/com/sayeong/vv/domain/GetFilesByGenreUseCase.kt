package com.sayeong.vv.domain

import com.sayeong.vv.model.FileResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilesByGenreUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(genres: List<String>): Flow<List<FileResource>> {
        return fileRepository.getFileListByGenre(genres)
    }

}