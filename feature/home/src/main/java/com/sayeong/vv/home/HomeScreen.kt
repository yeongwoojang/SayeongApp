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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
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

                TopSelection(modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
}

@Composable
private fun TopSelection(
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
                items = listOf("음악", "라디오", "33333333", "4444444444", "5555555555", "666666666","777777777,", "88888888", "988888888", "1088888888", "1188888888", "1288888888", "1388888888", "1488888888", "1588888888", "1688888888", "1788888888", "1888888888", "1988888888", "2088888888"),
                key = { it }
            ) {
                TopicButton(
                    name = it,
                    topicId = it,
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