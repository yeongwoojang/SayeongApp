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

    companion object {
        const val OPEN_PLAYER_ACTION = "com.sayeong.vv.OPEN_PLAYER"
        const val OPEN_PLAYER = "OPEN_PLAYER"
    }

    override fun onCreate() {
        super.onCreate()

        val sessionActivityIntent = Intent(OPEN_PLAYER_ACTION).apply {
            `package` = this@PlayBackService.packageName
            putExtra(OPEN_PLAYER, true)
        }

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Timber.i("TEST_LOG | onGetSession() | controllerInfo: $controllerInfo")
        return mediaSession
    }


    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        super.onUpdateNotification(session, startInForegroundRequired)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
    }
}