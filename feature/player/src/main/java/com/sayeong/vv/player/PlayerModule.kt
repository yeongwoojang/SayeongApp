package com.sayeong.vv.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    //_ SilenceSkippingAudioProcessor를 싱글톤으로 제공
    @Provides
    @Singleton
    fun provideSilenceSkippingAudioProcessor(): SilenceSkippingAudioProcessor {
        return SilenceSkippingAudioProcessor()
    }

    //_ CustomRendersFactory를 Hilt에서 제공하는 Provider를 생성
    //_ 이때 위에서 만든 SilenceSkippingAudioProcessor를 CustomRenderersFactory에 주입
    @UnstableApi
    @Provides
    @Singleton
    fun provideRenderersFactory(
        @ApplicationContext context: Context,
        silenceSkippingAudioProcessor: SilenceSkippingAudioProcessor
    ): RenderersFactory {
        return CustomRenderersFactory(context, silenceSkippingAudioProcessor)
    }

    @OptIn(UnstableApi::class)
    @Provides
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        ren: RenderersFactory): Player {
        return ExoPlayer.Builder(context)
            .setRenderersFactory(ren)
            .build()
    }
}