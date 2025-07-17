package kr.co.fastcampus.sayeongapp.di.demo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.fastcampus.sayeongapp.data.DemoRepositoryImpl
import kr.co.fastcampus.sayeongapp.domain.DemoRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoRepositoryModule {
    @Binds
    abstract fun bindDemoRepository(
        demoRepositoryImpl: DemoRepositoryImpl
    ): DemoRepository

}