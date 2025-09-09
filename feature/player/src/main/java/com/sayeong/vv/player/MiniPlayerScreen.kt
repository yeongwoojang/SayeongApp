import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
        (state.currentPosition.toFloat() / state.duration)
    } else {
        0f
    }

    //_ SayeongApp.kt에서 음악 재생이 시작되면 sheetPeekHeight를 172.dp 로 설정.
    //_ BottomSheet는 PartiallyExpanded상태에서 그 안에 그려지는 화면이 172.dp보다 작으면 화면을 이미 다 보여줬다고 판단하기 때문에드래그해도 확장하지 않음.
    //_ 따라서 해당 화면을 최대 높이로 설정하여 BottomSheet가 PartiallyExpanded 상태에서 아직 다 보여주지 못한 화면이 있다고 판단하게끔해서 드래그해서 위로 확장할 수 있게 함.
    Surface(
        modifier = modifier.fillMaxHeight()
            .clickable(onClick = onExpand), //_ 클릭하면 펼쳐지도록 함
        color = (state.dominantColor ?: Color.Black).copy(alpha = 0.8f)
    ) {
        Column {
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
                com.sayeong.vv.player.component.PlayPauseButton(player = player)
            }
            // 진행률 표시줄
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                gapSize = 0.dp,
                progress = { progress},
                strokeCap = StrokeCap.Square,
                color = Color.Red,
                trackColor = MaterialTheme.colorScheme.onSecondary,
                drawStopIndicator = {}
            )
        }
    }
}