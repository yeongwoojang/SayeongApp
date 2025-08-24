package com.sayeong.vv.domain

import javax.inject.Inject

class GetAlbumArtUseCase @Inject constructor(
    private val albumArtRepository: AlbumArtRepository
) {
    suspend operator fun invoke(resourceName: String): ByteArray? {
       return albumArtRepository.getAlbumArtByte(resourceName)
    }
}