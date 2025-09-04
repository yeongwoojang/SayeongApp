// feature/player/src/main/java/com/sayeong/vv/player/PlayerScreen.kt


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.sayeong.vv.player.model.LoadedState

@Composable
fun MiniPlayerScreen(
    state: LoadedState,
    player: Player,
    onExpand: () -> Unit, // 시트를 펼치는 람다 함수
    modifier: Modifier = Modifier
) {
    val progress = if (state.duration > 0) {
        state.currentPosition.toFloat() / state.duration
    } else {
        0f
    }

    Surface(
        modifier = modifier
            .clickable(onClick = onExpand)
            .navigationBarsPadding() // 하단 네비게이션 바 영역 확보
        , // 클릭하면 펼쳐지도록
        color = (state.dominantColor ?: Color.Black).copy(alpha = 0.8f)
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 앨범 아트
                state.albumArt?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Album Art",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(8.dp)
                    )
                }
                // 제목 / 아티스트
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.musicResource.originalName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = state.musicResource.artist ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
                // 재생/일시정지 버튼
                com.sayeong.vv.player.component.PlayPauseButton(player = player)
            }
            // 진행률 표시줄
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.BottomCenter),
                color = Color.Red,
                trackColor = Color.Gray
            )
        }
    }
}