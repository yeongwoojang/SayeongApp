package com.sayeong.vv.player.component

import androidx.annotation.OptIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberShuffleButtonState
import com.sayeong.vv.player.R

@OptIn(UnstableApi::class)
@Composable
internal fun ShuffleButton(player: Player, modifier: Modifier = Modifier) {
    val state = rememberShuffleButtonState(player)
    val icon = painterResource(R.drawable.shuffle_24px)
    val (tint, contentDescription) =
        if (state.shuffleOn) {
            Color.Red to "셔플 on"
        } else {
            Color.Black to "셔플 off"
        }

    IconButton(onClick = state::onClick, modifier = modifier, enabled = state.isEnabled) {
        Icon(
            icon,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}