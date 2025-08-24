package com.sayeong.vv.player.component


import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberRepeatButtonState
import com.sayeong.vv.player.R

@OptIn(UnstableApi::class)
@Composable
internal fun RepeatButton(player: Player, modifier: Modifier = Modifier) {
    val state = rememberRepeatButtonState(player)
    val icon = painterResource(R.drawable.repeat_24px)
    val tint = if (state.repeatModeState == Player.REPEAT_MODE_OFF) {
        Color.Black
    } else {
        Color.Red
    }

    val contentDescription = repeatModeContentDescription(state.repeatModeState)
    IconButton(onClick = state::onClick, modifier = modifier, enabled = state.isEnabled) {
        Icon(
            icon,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}

@Composable
private fun repeatModeContentDescription(repeatMode: @Player.RepeatMode Int): String {
    return when (repeatMode) {
        Player.REPEAT_MODE_OFF -> "반복 on"
        Player.REPEAT_MODE_ONE -> "반복 off"
        else -> "반복 on"
    }
}