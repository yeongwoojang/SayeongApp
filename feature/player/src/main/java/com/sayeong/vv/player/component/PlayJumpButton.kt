package com.sayeong.vv.player.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.sayeong.vv.player.R

@Composable
fun PlayJumpButton(
    player: Player,
    modifier: Modifier = Modifier,
    isNext: Boolean
) {

    val icon = if (isNext) {
        painterResource(R.drawable.skip_next_24px)
    } else {
        painterResource(R.drawable.skip_previous_24px)
    }
    IconButton(onClick = {
        if (isNext) {
            player.seekToNext()
        } else {
            player.seekToPrevious()
        }
    }, modifier = modifier) {
        Icon(
            modifier = modifier.size(50.dp),
            painter = icon,
            contentDescription = ""
        )
    }
}
