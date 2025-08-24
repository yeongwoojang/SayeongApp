package com.sayeong.vv.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayeong.vv.domain.GetMusicByGenreUseCase
import com.sayeong.vv.domain.GetTopicsUseCase
import com.sayeong.vv.home.model.MusicUiModel
import com.sayeong.vv.model.MusicResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase,
    private val getMusicByGenreUseCase: GetMusicByGenreUseCase
): ViewModel() {

    private val _topicUiState = MutableStateFlow<TopicUiState>(TopicUiState.Loading)
    val topicUiState = _topicUiState.asStateFlow()

    private val _musicUiState = MutableStateFlow<MusicUiState>(MusicUiState.Shown())
    val musicUiState = _musicUiState.asStateFlow()


    init {
        getTopics()
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
                val currentFileUiModels = (currentState as? MusicUiState.Shown)?.files ?: emptyList()
                val curFileUiModelMap = currentFileUiModels.associateBy { it.musicResource.originalName }

                newMusicRes = musicResources.filter {
                    !curFileUiModelMap.containsKey(it.originalName) || curFileUiModelMap[it.originalName]?.albumArt == null
                }

                val initialUiModels = musicResources.map { resource ->
                    val existingUiModel = curFileUiModelMap[resource.originalName]
                    existingUiModel?.copy(isArtLoading = false) ?: MusicUiModel(musicResource = resource)
                }

                _musicUiState.update {
                    (it as MusicUiState.Shown).copy(
                        files = initialUiModels
                    )
                }

                val bitmapJobs = newMusicRes.map { music ->
                    async { music to getBitMap(music) }
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
                        files = finalUiModels
                    )
                }
            }
        }
    }

    private fun getTopics() {
        viewModelScope.launch {
            getTopicsUseCase()
                .onStart {
                    _topicUiState.value = TopicUiState.Loading
                }
                .catch { throwable ->
                    _topicUiState.value = TopicUiState.Error(throwable.message)
                }
                .collect { topics ->
                    if (topics.isNotEmpty()) {
                        _topicUiState.value = TopicUiState.Shown(topics)
                    } else {
                        _topicUiState.value = TopicUiState.Shown()
                    }
                }
        }
    }

    fun selectTopic(topic: String) {
        val currentState = _topicUiState.value
        if (currentState is TopicUiState.Shown) {
            val oldSelectedIds = currentState.selectedTopics
            val newSelectedIds = if (oldSelectedIds.contains(topic)) {
                oldSelectedIds - topic
            } else {
                oldSelectedIds + topic
            }

            _topicUiState.update { currentState.copy(selectedTopics = newSelectedIds) }

        }
    }

    fun onDoneClick() {
        if (_topicUiState.value is TopicUiState.Shown) {
            val currentState = _topicUiState.value as TopicUiState.Shown
            _topicUiState.value = TopicUiState.Shown(
                topics = currentState.topics,
                selectedTopics = currentState.selectedTopics,
                isHide = !currentState.isHide
            )
        }
    }

    fun toggleBookMark(fileResource: MusicResource) {
        Timber.i("toggleBookMark() | fileResource: $fileResource")
        val currentState = _musicUiState.value
        if (currentState is MusicUiState.Shown) {
            val oldBookmarkedMusics = currentState.bookmarkedMusics
            val newBookMarkedMusics = if (fileResource in oldBookmarkedMusics) {
                oldBookmarkedMusics - fileResource
            } else {
                oldBookmarkedMusics + fileResource
            }
            _musicUiState.update { (it as MusicUiState.Shown).copy(bookmarkedMusics = newBookMarkedMusics) }
        }
    }

    private suspend fun getBitMap(musicResource: MusicResource): Bitmap? {
        return withContext(Dispatchers.IO) {
            val url = "http://10.0.2.2:3000/uploads/${musicResource.originalName}"
            val retriever = MediaMetadataRetriever()
            try {
                // URL로부터 데이터를 설정합니다. 두 번째 인자는 헤더 정보입니다.
                retriever.setDataSource(url, HashMap<String, String>())

                // getEmbeddedPicture()를 통해 이미지 데이터를 byte 배열로 가져옵니다.
                val artBytes = retriever.embeddedPicture

                if (artBytes != null) {
                    // byte 배열을 Bitmap으로 변환하여 반환합니다.
                    val bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size)
                    bitmap
                } else {
                    null // 앨범 아트가 없는 경우 null 반환
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null // 에러 발생 시 null 반환
            } finally {
                // 리소스 해제는 필수입니다.
                retriever.release()
            }
        }
    }
}