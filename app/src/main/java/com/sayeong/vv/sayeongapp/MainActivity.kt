package com.sayeong.vv.sayeongapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sayeong.vv.home.HomeScreen
import com.sayeong.vv.designsystem.theme.SayeongAppTheme
import com.sayeong.vv.player.PlayBackService
import com.sayeong.vv.sayeongapp.ui.SayeongApp
import com.sayeong.vv.sayeongapp.ui.rememberSayeongAppState
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var shouldLaunchPlayer by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)
        setContent {
            SayeongAppTheme {
                val appState = rememberSayeongAppState()
                SayeongApp(
                    appState = appState,
                    shouldLaunchPlayer = shouldLaunchPlayer,
                    onPlayerLaunched = { shouldLaunchPlayer = false }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        shouldLaunchPlayer = intent?.getBooleanExtra(PlayBackService.OPEN_PLAYER, false) ?: false
    }
}
