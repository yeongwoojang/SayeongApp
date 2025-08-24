package com.sayeong.vv.data.di

import androidx.collection.LruCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {
    @Provides
    @Singleton
    fun provideMemoryCache(): LruCache<String, ByteArray> {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt() //_ 4MB
        val cacheSize = maxMemory / 8 //_ 메모리 사이즈의 1/8만 사용

        return LruCache(cacheSize)
    }
}