package com.sayeong.vv.player.component


import android.view.Gravity
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlaybackSpeedState

@OptIn(UnstableApi::class)
@Composable
internal fun PlaybackSpeedPopUpButton(
    player: Player,
    modifier: Modifier = Modifier,
    speedSelection: List<Float> = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f),
) {
    val state = rememberPlaybackSpeedState(player)
    var openDialog by remember { mutableStateOf(false) }
    TextButton(onClick = { openDialog = true }, modifier = modifier, enabled = state.isEnabled) {
        BasicText("%.1fx".format(state.playbackSpeed))
    }
    if (openDialog) {
        BottomDialogOfChoices(
            currentSpeed = state.playbackSpeed,
            choices = speedSelection,
            onDismissRequest = { openDialog = false },
            onSelectChoice = state::updatePlaybackSpeed,
        )
    }
}

@Composable
private fun BottomDialogOfChoices(
    currentSpeed: Float,
    choices: List<Float>,
    onDismissRequest: () -> Unit,
    onSelectChoice: (Float) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val dialogWindowProvider = LocalView.current.parent as? DialogWindowProvider
        dialogWindowProvider?.window?.let { window ->
            window.setGravity(Gravity.BOTTOM) // Move down, by default dialogs are in the centre
            window.setDimAmount(0f) // Remove dimmed background of ongoing playback
        }

        Box(modifier = Modifier.wrapContentSize().background(Color.LightGray)) {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                choices.forEach { speed ->
                    TextButton(
                        onClick = {
                            onSelectChoice(speed)
                            onDismissRequest()
                        }
                    ) {
                        var fontWeight = FontWeight(400)
                        if (speed == currentSpeed) {
                            fontWeight = FontWeight(1000)
                        }
                        Text("%.1fx".format(speed), fontWeight = fontWeight)
                    }
                }
            }
        }
    }
}