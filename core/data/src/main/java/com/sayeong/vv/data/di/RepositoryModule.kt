package com.sayeong.vv.data.di

import com.sayeong.vv.data.impl.AlbumArtRepositoryImpl
import com.sayeong.vv.data.impl.MusicRepositoryImpl
import com.sayeong.vv.data.impl.TopicRepositoryImpl
import com.sayeong.vv.domain.AlbumArtRepository
import com.sayeong.vv.domain.MusicRepository
import com.sayeong.vv.domain.TopicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindGetMusicRepository(repository: MusicRepositoryImpl): MusicRepository

    @Binds
    abstract fun bindGetTopicRepository(repository: TopicRepositoryImpl): TopicRepository

    @Binds abstract fun bindGetAlbumArtRepository(repository: AlbumArtRepositoryImpl): AlbumArtRepository
}