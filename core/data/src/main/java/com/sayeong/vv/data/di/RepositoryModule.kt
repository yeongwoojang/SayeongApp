package com.sayeong.vv.data.di

import com.sayeong.vv.data.impl.FileRepositoryImpl
import com.sayeong.vv.data.impl.TopicRepositoryImpl
import com.sayeong.vv.domain.FileRepository
import com.sayeong.vv.domain.TopicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindGetFileRepository(repository: FileRepositoryImpl): FileRepository

    @Binds
    abstract fun bindGetTopicRepository(repository: TopicRepositoryImpl): TopicRepository
}