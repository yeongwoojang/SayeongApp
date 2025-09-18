package com.sayeong.vv.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PlayBackService: MediaSessionService() {
    @Inject
    lateinit var player: Player
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        // 1. MainActivity를 열도록 Intent 생성
        val sessionActivityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.apply {
            // "player" 화면을 열라는 추가 정보 담기
            putExtra("LAUNCH_PLAYER", true)
        }

        // 2. PendingIntent 생성
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        Timber.i("TEST_LOG | gogogogo")
        // 3. MediaSession 빌드 시 PendingIntent 설정
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Timber.i("TEST_LOG | onGetSession() | controllerInfo: $controllerInfo")
        return mediaSession
    }


    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        Timber.i("TEST_LOG | onUpdateNotification() | startInForegroundRequired: $startInForegroundRequired")
        super.onUpdateNotification(session, startInForegroundRequired)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.i("TEST_LOG | onTaskRemoved()")
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        Timber.i("TEST_LOG | onDestroy()")
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}