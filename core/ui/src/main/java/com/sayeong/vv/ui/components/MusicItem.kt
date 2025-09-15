package com.sayeong.vv.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults.iconToggleButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.ui.MusicUiModel
import com.sayeong.vv.ui.R

@Composable
fun MusicItem(
    isBookmarked: Boolean,
    musicUiModel: MusicUiModel,
    modifier: Modifier,
    onToggleBookMark: (MusicResource) -> Unit = {},
    onMusicClick: () -> Unit = {},
    onPlayMusic: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
//        onClick = onMusicClick,
        onClick = onPlayMusic
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 앨범 아트 이미지
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                // 개별 이미지 로딩 스피너
                if (musicUiModel.isArtLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
                // 앨범 아트가 있으면 표시
                musicUiModel.albumArt?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "${musicUiModel.musicResource.originalName} 앨범 아트",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // 파일 정보 (제목, 아티스트 등)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = musicUiModel.musicResource.originalName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = musicUiModel.musicResource.artist ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledIconToggleButton(
                checked = isBookmarked,
                onCheckedChange = { onToggleBookMark(musicUiModel.musicResource) },
                colors = iconToggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.onSecondary,
                ),
                modifier = Modifier.size(30.dp),
            ) {
                val imageResource = if (isBookmarked) {
                    painterResource(R.drawable.bookmark_filled_24px)
                } else {
                    painterResource(R.drawable.bookmark_24px)
                }
                Icon(
                    imageResource,
                    contentDescription = "bookmark",
                )
            }
        }
    }
}