package com.sayeong.vv.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink


/**
 * ExoPlayer의 렌더러(Renderer) 생성 방식을 커스터마이징하는 팩토리 클래스.
 * 여기서는 기본 오디오 처리 파이프라인(AudioSink)에 우리가 만든
 * SilenceSkippingAudioProcessor를 추가하는 역할을 합니다.
 *
 * @param context 애플리케이션 컨텍스트
 * @param silenceSkippingAudioProcessor ExoPlayer에 추가할 커스텀 오디오 프로세서
 */
@OptIn(UnstableApi::class)
class CustomRenderersFactory(
    private val context: Context,
    private val silenceSkippingAudioProcessor: SilenceSkippingAudioProcessor
): DefaultRenderersFactory(context) {


    override fun buildAudioSink(
        context: Context,
        enableFloatOutput: Boolean,
        enableAudioTrackPlaybackParams: Boolean
    ): AudioSink {
        // DefaultAudioSink를 만들 때, setAudioProcessors를 통해
        // 우리가 만든 SilenceSkippingAudioProcessor를 배열에 담아 전달합니다.
        // 이렇게 하면 모든 오디오 데이터가 우리 프로세서를 거쳐 가게 됩니다.
        return DefaultAudioSink.Builder(context)
            .setAudioProcessors(arrayOf(silenceSkippingAudioProcessor))
            .build()
    }
}