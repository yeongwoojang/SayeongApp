package com.sayeong.vv.domain


interface AlbumArtRepository {
    suspend fun getAlbumArtByte(resourceName: String): ByteArray?
}