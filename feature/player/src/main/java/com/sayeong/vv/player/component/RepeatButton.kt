package com.sayeong.vv.player.component


import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberRepeatButtonState

@OptIn(UnstableApi::class)
@Composable
internal fun RepeatButton(player: Player, modifier: Modifier = Modifier) {
    val state = rememberRepeatButtonState(player)
    val icon = repeatModeIcon(state.repeatModeState)
    val contentDescription = repeatModeContentDescription(state.repeatModeState)
    IconButton(onClick = state::onClick, modifier = modifier, enabled = state.isEnabled) {
        Icon(icon, contentDescription = contentDescription, modifier = modifier)
    }
}

private fun repeatModeIcon(repeatMode: @Player.RepeatMode Int): ImageVector {
    return when (repeatMode) {
        Player.REPEAT_MODE_OFF -> Icons.Default.Call
        Player.REPEAT_MODE_ONE -> Icons.Default.Done
        else -> Icons.Default.Call
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