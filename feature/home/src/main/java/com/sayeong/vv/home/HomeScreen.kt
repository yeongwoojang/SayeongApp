package com.sayeong.vv.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults.iconToggleButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayeong.vv.designsystem.component.DynamicAsyncImage
import com.sayeong.vv.designsystem.component.SayeongButton
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.model.TopicResource
import com.sayeong.vv.ui.MusicUiModel
import com.sayeong.vv.ui.components.MusicItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMusicClick: (MusicResource) -> Unit,
    omMusicPlay: (List<MusicResource>) -> Unit
) {

    val topicUiState by viewModel.topicUiState.collectAsStateWithLifecycle()
    val musicUiState by viewModel.musicUiState.collectAsStateWithLifecycle()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 24.dp,
        modifier = Modifier
    ) {
        var buttonText = "Done"
        var isButtonEnable = false
        item(span = StaggeredGridItemSpan.FullLine) {
            Column {
                when (val state = topicUiState) {
                    is TopicUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is TopicUiState.Error -> {
                        val errorMsg = state.message
                        if (errorMsg != null) {
                            Text(
                                text = "데이터를 불러오는데 실패했습니다: $errorMsg",
                                modifier = Modifier.padding(top = 8.dp)
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    is TopicUiState.Shown -> {
                        TopSectionContent(
                            topics = state.topics,
                            selectedTopics = state.selectedTopics,
                            isHide = state.isHide,
                            modifier = Modifier.padding(top = 8.dp),
                            onTopicClick = viewModel::selectTopic
                        )

                        buttonText = if (state.isHide) "Refresh" else "Done"

                        isButtonEnable = state.selectedTopics.isNotEmpty()
                    }
                    else -> {}
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SayeongButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = viewModel::onDoneClick,
                        enable = isButtonEnable
                    ) {
                        Text(text = buttonText)
                    }
                }
            }
        }

        musicList(
            musicUiState,
            onToggleBookMark = viewModel::toggleBookMark,
            onMusicClick = onMusicClick,
            onMusicPlay = omMusicPlay,
        )
    }
}

private fun LazyStaggeredGridScope.musicList(
    uiState: MusicUiState,
    onToggleBookMark: (MusicResource) -> Unit,
    onMusicClick: (MusicResource) -> Unit,
    onMusicPlay: (List<MusicResource>) -> Unit,
) {
    if (uiState is MusicUiState.Shown) {
        if (uiState.musics.isNotEmpty()) {
            val musicUiModels = uiState.musics
            items(
                items = musicUiModels,
                key = { it.id }
            ) { music ->
                MusicItem(
                    isBookmarked = music.musicResource in uiState.bookmarkedMusics,
                    musicUiModel = music,
                    modifier = Modifier.fillMaxWidth().animateItem(),
                    onToggleBookMark = { onToggleBookMark(music.musicResource) },
                    onMusicClick = { onMusicClick(music.musicResource) },
                    onPlayMusic = {
                        val curIdx = musicUiModels.indexOf(music)
                        onMusicPlay(musicUiModels.drop(curIdx).map { it.musicResource })
                    }
                )
            }
        }
    }
}

//@Composable
//private fun MusicItem(
//    isBookmarked: Boolean,
//    musicUiModel: MusicUiModel,
//    modifier: Modifier,
//    onToggleBookMark: () -> Unit = {},
//    onMusicClick: () -> Unit = {}
//) {
//    Surface(
//        modifier = modifier,
//        shape = RoundedCornerShape(8.dp),
//        color = MaterialTheme.colorScheme.surface,
//        onClick = onMusicClick
//    ) {
//        Row(
//            modifier = Modifier.padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // 앨범 아트 이미지
//            Box(
//                modifier = Modifier
//                    .size(120.dp)
//                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp)),
//                contentAlignment = Alignment.Center
//            ) {
//                // 개별 이미지 로딩 스피너
//                if (musicUiModel.isArtLoading) {
//                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
//                }
//                // 앨범 아트가 있으면 표시
//                musicUiModel.albumArt?.let { bitmap ->
//                    Image(
//                        bitmap = bitmap.asImageBitmap(),
//                        contentDescription = "${musicUiModel.musicResource.originalName} 앨범 아트",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//            }
//
//            Spacer(Modifier.width(16.dp))
//
//            // 파일 정보 (제목, 아티스트 등)
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = musicUiModel.musicResource.originalName,
//                    style = MaterialTheme.typography.titleMedium
//                )
//                Text(
//                    text = musicUiModel.musicResource.artist ?: "Unknown Artist",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//
//            FilledIconToggleButton(
//                checked = isBookmarked,
//                onCheckedChange = { onToggleBookMark() },
//                colors = iconToggleButtonColors(
//                    checkedContainerColor = MaterialTheme.colorScheme.onSecondary,
//                ),
//                modifier = Modifier.size(30.dp),
//            ) {
//                val imageResource = if (isBookmarked) {
//                    painterResource(R.drawable.bookmark_filled_24px)
//                } else {
//                    painterResource(R.drawable.bookmark_24px)
//                }
//                Icon(
//                    imageResource,
//                    contentDescription = "bookmark",
//                )
//            }
//        }
//    }
//}

@Composable
private fun TopSectionContent(
    topics: List<TopicResource>,
    selectedTopics: Set<String>,
    isHide: Boolean,
    modifier: Modifier,
    onTopicClick: (String) -> Unit
) {
    if (topics.isNotEmpty() && !isHide) {
        Text(
            text = stringResource(R.string.feature_home_screen_guidance_title),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.feature_home_screen_guidance_sub_title),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            style = MaterialTheme.typography.titleMedium
        )

        TopSelection(
            topics = topics,
            modifier = modifier.padding(bottom = 8.dp),
            selectedTopics = selectedTopics,
            onTopicClick = onTopicClick
        )
    }
}

@Composable
private fun TopSelection(
    topics: List<TopicResource>,
    modifier: Modifier = Modifier,
    selectedTopics: Set<String>,
    onTopicClick: (String) -> Unit = {}
) {
    val lazyGridState = rememberLazyGridState()

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        LazyHorizontalGrid(
            state = lazyGridState,
            rows = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 24.dp),
            modifier = Modifier
                .heightIn(max = max(240.dp, with(LocalDensity.current) { 240.sp.toDp() }))
                .fillMaxWidth(),
        ) {
            items(
                items = topics,
                key = { it.id }
            ) { topic ->
                TopicButton(
                    name = topic.title,
                    imgUrl = topic.imageUrl,
                    isSelected = topic.title in selectedTopics,
                    onClick = onTopicClick
                )
            }
        }
    }
}

@Composable
private fun TopicButton(
    name: String,
    imgUrl: String,
    isSelected: Boolean,
    onClick: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(312.dp)
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        color = MaterialTheme.colorScheme.surface,
        selected = isSelected,
        onClick = { onClick(name) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 8.dp)
        ) {
            DynamicAsyncImage(
                imageUrl = imgUrl,
                contentDescription = "",
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
            )
            FilledIconToggleButton(
                checked = isSelected,
                onCheckedChange = { },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.check_24px),
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview
@Composable
fun TopicButtonPreview() {
    TopicButton(
        "힙합",
        "",
        true,
        {}
        )
}
