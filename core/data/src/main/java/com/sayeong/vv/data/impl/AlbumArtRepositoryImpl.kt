package com.sayeong.vv.data.impl

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.collection.LruCache
import com.sayeong.vv.domain.AlbumArtRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AlbumArtRepositoryImpl @Inject constructor(
    private val memoryCache: LruCache<String, ByteArray>
) : AlbumArtRepository {
    override suspend fun getAlbumArtByte(resourceName: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            val cacheKey = resourceName
            val url = "http://10.0.2.2:3000/uploads/$cacheKey"

            val cacheData = memoryCache.get(cacheKey)
            if (cacheData != null) {
                return@withContext cacheData
            }

            val retriever = MediaMetadataRetriever()
            try {
                // URL로부터 데이터를 설정합니다. 두 번째 인자는 헤더 정보입니다.
                retriever.setDataSource(url, HashMap<String, String>())

                // getEmbeddedPicture()를 통해 이미지 데이터를 byte 배열로 가져옵니다.
                val artBytes = retriever.embeddedPicture
                if (artBytes != null) {
                    memoryCache.put(cacheKey, artBytes)
                }
                artBytes
            } catch (e: Exception) {
                e.printStackTrace()
                null // 에러 발생 시 null 반환
            } finally {
                // 리소스 해제는 필수입니다.
                retriever.release()
            }
        }
    }
}