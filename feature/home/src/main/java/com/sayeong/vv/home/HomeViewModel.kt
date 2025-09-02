package com.sayeong.vv.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayeong.vv.domain.GetAlbumArtUseCase
import com.sayeong.vv.domain.GetMusicByGenreUseCase
import com.sayeong.vv.domain.GetTopicsUseCase
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.ui.MusicUiModel
import com.sayeong.vv.ui.utils.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase,
    private val getMusicByGenreUseCase: GetMusicByGenreUseCase,
    private val getAlbumArtUseCase: GetAlbumArtUseCase
) : ViewModel() {

    private val _selectedTopics = MutableStateFlow<Set<String>>(emptySet())
    private val _isHide = MutableStateFlow<Boolean>(false)

    val topicUiState: StateFlow<TopicUiState> = combine(
        flow {
            emitAll(getTopicsUseCase())
        },
        _selectedTopics,
        _isHide
    ) { topics, selectedTopics, isHide ->
        TopicUiState.Shown(
            topics = topics,
            selectedTopics = selectedTopics,
            isHide = isHide
        )
    }.onStart<TopicUiState> { emit(TopicUiState.Loading) }
        .catch { throwable -> emit(TopicUiState.Error(throwable.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TopicUiState.Loading
        )

    private val bookmarkedMusics = MutableStateFlow<Set<MusicResource>>(emptySet())

    private val musicResourcesFlow: Flow<List<MusicResource>> =
        topicUiState.flatMapLatest { topicState ->
            if (topicState is TopicUiState.Shown && topicState.selectedTopics.isNotEmpty()) {
                getMusicByGenreUseCase(topicState.selectedTopics.toList())
            } else {
                flowOf(emptyList())
            }
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            replay = 1
        )


    private val albumArtCache: StateFlow<Map<String, Bitmap?>> = musicResourcesFlow
        .scan(emptyMap<String, Bitmap?>()) { cachedMap, musicResources ->
            val fetchToAlbumArts = musicResources.filter { musicResource ->
                !cachedMap.containsKey(musicResource.originalName)
            }
            if (fetchToAlbumArts.isEmpty()) {
                cachedMap
            } else {
                val map = withContext(Dispatchers.IO) {
                    musicResources.map {
                        async { it.originalName to getAlbumArtUseCase(it.originalName).toBitmap() }
                    }.awaitAll().toMap()
                }

                cachedMap + map
            }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val musicUiState: StateFlow<MusicUiState> = combine(
        musicResourcesFlow,
        bookmarkedMusics,
        albumArtCache
    ) { musicResources, bookmarkedMusics, albumArtCache ->
        val musics = musicResources.map {
            val albumArt = albumArtCache[it.originalName]
            val isArtLoading = !albumArtCache.containsKey(it.originalName)

            MusicUiModel(
                musicResource = it,
                albumArt = albumArt,
                isArtLoading = isArtLoading
            )
        }

        MusicUiState.Shown(
            musics = musics,
            bookmarkedMusics = bookmarkedMusics
        )
    }.catch<MusicUiState> {
        emit(MusicUiState.Error(it.message))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MusicUiState.Shown()
    )

    fun selectTopic(topic: String) {
        _selectedTopics.update { currentState ->
            if (currentState.contains(topic)) {
                currentState - topic
            } else {
                currentState + topic
            }
        }
    }

    fun onDoneClick() {
        _isHide.update { !it }
    }

    fun toggleBookMark(musicResource: MusicResource) {
        Timber.i("toggleBookMark() | musicResource: $musicResource")

        bookmarkedMusics.update {
            if (musicResource in it) {
                it - musicResource
            } else {
                it + musicResource
            }
        }
    }
}