package com.sayeong.vv.player.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.common.Player

@Composable
internal fun ExtraControls(player: Player, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaybackSpeedPopUpButton(player)
        PlayJumpButton(player, isNext = false)
        PlayPauseButton(player)
        PlayJumpButton(player, isNext = true)
        ShuffleButton(player)
        RepeatButton(player)
    }
}