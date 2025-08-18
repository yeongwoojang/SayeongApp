package com.sayeong.vv.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sayeong.vv.designsystem.component.DynamicAsyncImage
import com.sayeong.vv.model.TopicResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 24.dp,
        modifier = Modifier
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            Column {
                Text(
                    text = stringResource(R.string.feature_home_screen_guidance_title),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 24.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.feature_home_screen_guidance_sub_title),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 24.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                TopSectionContent(
                    uiState = uiState,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun TopSectionContent(
    uiState: HomeUiState,
    modifier: Modifier
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    uiState.error?.let { errorMsg ->
        Text(
            text = "데이터를 불러오는데 실패했습니다: $errorMsg",
            modifier = modifier.fillMaxWidth().padding(24.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
    }

    if (uiState.topics.isNotEmpty()) {
        TopSelection(
            topics = uiState.topics,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun TopSelection(
    topics: List<TopicResource>,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        LazyHorizontalGrid(
            modifier = Modifier.
            heightIn(max = max(240.dp, with(LocalDensity.current) { 240.sp.toDp() }))
                .fillMaxWidth(),
            state = lazyGridState,
            rows = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(24.dp),
        ) {
            items(
                items = topics,
                key = { it.id }
            ) {
                TopicButton(
                    name = it.title,
                    topicId = "${it.id}",
                    imgUrl = it.imageUrl,
                    isSelected = false,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun TopicButton(
    name: String,
    topicId: String,
    imgUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.width(300.dp)
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        color = MaterialTheme.colorScheme.surface,
        selected = isSelected,
        onClick = { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
            )
            DynamicAsyncImage(
                imageUrl = imgUrl,
                contentDescription = "",
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun SingleTopicButton(
    name: String,
    topicId: String,
    imageUrl: String,
    isSelected: Boolean,
    onClick: (String, Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(312.dp)
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        color = MaterialTheme.colorScheme.surface,
        selected = isSelected,
        onClick = {
            onClick(topicId, !isSelected)
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 8.dp),
        ) {
//            TopicIcon(
//                imageUrl = imageUrl,
//            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
            )
//            NiaIconToggleButton(
//                checked = isSelected,
//                onCheckedChange = { checked -> onClick(topicId, checked) },
//                icon = {
//                    Icon(
//                        imageVector = NiaIcons.Add,
//                        contentDescription = name,
//                    )
//                },
//                checkedIcon = {
//                    Icon(
//                        imageVector = NiaIcons.Check,
//                        contentDescription = name,
//                    )
//                },
//            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {

}

@Composable
fun ThirdScreen() {
    Text("Third")
}