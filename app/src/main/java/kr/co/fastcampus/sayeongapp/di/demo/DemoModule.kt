package kr.co.fastcampus.sayeongapp.di.demo

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.fastcampus.sayeongapp.domain.DemoRepository
import kr.co.fastcampus.sayeongapp.domain.DemoUseCase

@Module
@InstallIn(SingletonComponent::class)
class DemoModule {

    @Provides
    fun getDemoUseCase(repository: DemoRepository): DemoUseCase {
        return DemoUseCase(repository)
    }
}