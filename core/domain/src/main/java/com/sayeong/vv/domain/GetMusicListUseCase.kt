package com.sayeong.vv.domain

import com.sayeong.vv.model.MusicResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMusicListUseCase @Inject constructor(
    private val repository: FileRepository
){
    suspend operator fun invoke(): Flow<List<MusicResource>> {
        return repository.getMusicList()
    }
}