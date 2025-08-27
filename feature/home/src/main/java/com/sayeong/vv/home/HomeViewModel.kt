package com.sayeong.vv.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayeong.vv.domain.GetAlbumArtUseCase
import com.sayeong.vv.domain.GetMusicByGenreUseCase
import com.sayeong.vv.domain.GetTopicsUseCase
import com.sayeong.vv.home.model.MusicUiModel
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.model.TopicResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
            delay(3000)
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


    private val _musicUiState = MutableStateFlow<MusicUiState>(MusicUiState.Shown())
    val musicUiState = _musicUiState.asStateFlow()

//    private val _bookmarkedMusics = MutableStateFlow<Set<MusicResource>>(emptySet())
//    val musicUiState = topicUiState.flatMapLatest { topicState ->
//        if (topicState is TopicUiState.Shown && topicState.selectedTopics.isNotEmpty()) {
//            getMusicByGenreUseCase(topicState.selectedTopics.toList())
//                .catch { throwable ->
//                    _musicUiState.value = MusicUiState.Error(throwable.message)
//                }
//        } else {
//            flowOf(emptyList())
//        }
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = MusicUiState.Shown()
//    )
//

    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            topicUiState.flatMapLatest { topicState ->
                if (topicState is TopicUiState.Shown && topicState.selectedTopics.isNotEmpty()) {
                    getMusicByGenreUseCase(topicState.selectedTopics.toList())
                        .catch { throwable ->
                            _musicUiState.value = MusicUiState.Error(throwable.message)
                        }
                } else {
                    flowOf(emptyList())
                }
            }.collect { musicResources ->
                var newMusicRes = emptyList<MusicResource>()
                val currentState = _musicUiState.value
                val currentFileUiModels =
                    (currentState as? MusicUiState.Shown)?.musics ?: emptyList()
                val curFileUiModelMap =
                    currentFileUiModels.associateBy { it.musicResource.originalName }

                newMusicRes = musicResources.filter {
                    !curFileUiModelMap.containsKey(it.originalName) || curFileUiModelMap[it.originalName]?.albumArt == null
                }

                val initialUiModels = musicResources.map { resource ->
                    val existingUiModel = curFileUiModelMap[resource.originalName]
                    existingUiModel?.copy(isArtLoading = false)
                        ?: MusicUiModel(musicResource = resource)
                }

                _musicUiState.update {
                    (it as MusicUiState.Shown).copy(
                        musics = initialUiModels
                    )
                }

                val bitmapJobs = newMusicRes.map { music ->
                    async { music to getBitMap(music.originalName) }
                }

                val newAlbumArtMap = bitmapJobs.awaitAll().toMap()
                val finalUiModels = musicResources.map { resource ->
                    if (newAlbumArtMap.containsKey(resource)) {
                        MusicUiModel(
                            musicResource = resource,
                            albumArt = newAlbumArtMap[resource],
                            isArtLoading = false
                        )
                    } else {
                        curFileUiModelMap[resource.originalName]!!
                    }
                }

                _musicUiState.update {
                    (it as MusicUiState.Shown).copy(
                        musics = finalUiModels
                    )
                }
            }
        }
    }

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
        val currentState = _musicUiState.value
        if (currentState is MusicUiState.Shown) {
            val oldBookmarkedMusics = currentState.bookmarkedMusics
            val newBookMarkedMusics = if (musicResource in oldBookmarkedMusics) {
                oldBookmarkedMusics - musicResource
            } else {
                oldBookmarkedMusics + musicResource
            }
            _musicUiState.update { (it as MusicUiState.Shown).copy(bookmarkedMusics = newBookMarkedMusics) }
        }
    }

    private suspend fun getBitMap(resourceName: String): Bitmap? {
        val albumArtByte = getAlbumArtUseCase(resourceName)
        val bitmap = if (albumArtByte != null) {
            BitmapFactory.decodeByteArray(albumArtByte, 0, albumArtByte.size)
        } else {
            null
        }
        return bitmap
    }
}