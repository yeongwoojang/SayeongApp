package com.sayeong.vv.player

import androidx.annotation.OptIn
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.C
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import timber.log.Timber
import java.nio.ByteBuffer
import kotlin.math.abs

@OptIn(UnstableApi::class)
class SilenceSkippingAudioProcessor: BaseAudioProcessor() {
    // 16비트 오디오 샘플에서 '무음'으로 간주할 소리의 최대 크기 (진폭)
    private val silenceThreshold: Short = 1500

    // '무음 구간'으로 인정하기 위한 최소 지속 시간 (마이크로초 단위)
    private val minSilenceDurationUs: Long = 500_000

    private var consecutiveSilenceUs: Long = 0
    var isEnabled: Boolean = false


    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            Timber.e("inputAudioFormat encoding is not PCM_16BIT")
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }

        return inputAudioFormat
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!isEnabled) {
            // [수정된 부분] 기능이 비활성화 상태일 때도 안전하게 버퍼를 복사합니다.
            if (inputBuffer.hasRemaining()) {
                // 출력 버퍼를 새로 요청하고, 원본(input) 버퍼의 내용을 복사합니다.
                val outputBuffer = replaceOutputBuffer(inputBuffer.remaining())
                outputBuffer.put(inputBuffer)
                outputBuffer.flip()
            }
            return
        }


        // --- 기능이 활성화 상태일 때의 로직 (이전과 동일) ---
        // isSilent 함수는 버퍼를 읽기만 하므로 원본 버퍼의 상태를 보존할 필요가
        // 사실상 없습니다. getShort()는 position을 바꾸지 않습니다.
        // 하지만 더 명확한 코드를 위해 mark/reset을 유지하거나 제거할 수 있습니다.
        val isBufferSilent = isSilent(inputBuffer)
        if (isBufferSilent) {
            consecutiveSilenceUs = getBufferDurationUs(inputBuffer.remaining())
            inputBuffer.position(inputBuffer.limit())
        } else {
            consecutiveSilenceUs = 0
            if (inputBuffer.hasRemaining()) {
                val outputBuffer = replaceOutputBuffer(inputBuffer.remaining())
                outputBuffer.put(inputBuffer)
                outputBuffer.flip()
            }
        }
    }

    override fun onReset() {
        consecutiveSilenceUs = 0
    }

    private fun isSilent(buffer: ByteBuffer): Boolean {
        // 1. 처리할 데이터가 남아있는지 확인
        if (!buffer.hasRemaining()) {
            // 버퍼가 비어있다면, 당연히 무음이므로 true를 반환하고 즉시 종료합니다.
            return true
        }
        // 2. 버퍼의 현재 읽기 위치를 별도의 변수에 복사
        // buffer.position()은 버퍼의 내부 상태를 바꾸지 않고 값만 가져옵니다.
        var position = buffer.position()

        // 3. 현재 위치부터 버퍼의 데이터 끝까지 반복
        while (position < buffer.limit()) {
            // 4. 특정 위치(position)의 오디오 샘플 값(2바이트)을 읽어옴
            // getShort(index)는 버퍼의 내부 position을 변경하지 않습니다.
            val sampleValue = buffer.getShort(position).toInt()

            // 5. 샘플 값의 절댓값을 구해서 소리의 크기를 판단
            // abs()는 소리의 크기가 양수이든 음수이든 상관없이 순수한 크기만 비교하게 해줍니다.
            if (abs(sampleValue) > silenceThreshold) {
                // 6. 소리의 크기가 우리가 정한 기준(silenceThreshold)보다 크다면,
                // 이 버퍼는 '소리가 있는' 버퍼입니다.
                // 더 이상 검사할 필요가 없으므로 false를 반환하고 즉시 종료합니다.
                return false
            }

            // 7. 현재 위치의 샘플이 무음이라면, 다음 샘플 위치로 이동합니다.
            // 16비트 PCM 데이터는 샘플 하나가 2바이트이므로 2씩 더해줍니다.
            position += 2
        }

        // 8. while 루프를 무사히 다 통과했다면?
        // 버퍼의 모든 샘플이 기준보다 작았다는 의미이므로,
        // 이 버퍼는 '완전한 무음'입니다. true를 반환합니다.
        return true
    }

    /**
     * 버퍼의 바이트 길이를 기반으로 오디오의 지속 시간(마이크로초)을 계산하는 헬퍼 함수
     */
    private fun getBufferDurationUs(byteSize: Int): Long {
        val format = inputAudioFormat
        if (format.sampleRate == 0) return 0

        // [수정된 부분] 한 프레임의 크기 계산 시 bytesPerFrame 대신 Util.getPcmFrameSize 사용 권장
        // 하지만 간단하게 채널 수 * 2 (16비트=2바이트)로 계산해도 대부분의 경우 동일하게 동작합니다.
        val frameSize = format.channelCount * 2
        if (frameSize == 0) return 0

        val frameCount = byteSize / frameSize
        return (frameCount * 1_000_000L) / format.sampleRate
    }
}