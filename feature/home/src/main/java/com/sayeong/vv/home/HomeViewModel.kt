package com.sayeong.vv.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayeong.vv.domain.GetFilesByGenreUseCase
import com.sayeong.vv.domain.GetTopicsUseCase
import com.sayeong.vv.home.model.FileUiModel
import com.sayeong.vv.model.FileResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase,
    private val getFilesByGenreUseCase: GetFilesByGenreUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getTopics()
    }

    private fun getTopics() {
        viewModelScope.launch {
            getTopicsUseCase()
                .onStart {
                    _uiState.value = HomeUiState.Loading
                }
                .catch { throwable ->
                    _uiState.value = HomeUiState.Error(throwable.message)
                }
                .collect { topics ->
                    if (topics.isNotEmpty()) {
                        _uiState.value = HomeUiState.Shown(topics)
                    } else {
                        _uiState.value = HomeUiState.NotShownTopic
                    }
                }
        }
    }

    fun onTopicClick(topic: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Shown) {
            val oldSelectedIds = currentState.selectedTopics
            val newSelectedIds = if (oldSelectedIds.contains(topic)) {
                oldSelectedIds - topic
            } else {
                oldSelectedIds + topic
            }

            _uiState.update { currentState.copy(selectedTopics = newSelectedIds) }
            if (newSelectedIds.isNotEmpty()) {
                selectTopic()
            } else {
                _uiState.update {
                    (it as HomeUiState.Shown).copy(
                        files = emptyList()
                    )
                }
            }
        }
    }

    private fun selectTopic() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Shown && currentState.selectedTopics.isNotEmpty()) {
            requestFileList(currentState.selectedTopics.toList())
        }
    }

    fun onDoneClick() {
       //TODO Topic 목록을 제거
        _uiState.update { HomeUiState.NotShownTopic }
    }

    private fun requestFileList(genres: List<String>) {
        viewModelScope.launch {
            getFilesByGenreUseCase(genres)
                .catch { throwable ->
                    _uiState.update {
                        (it as HomeUiState.Shown).copy(
                            contentError = throwable.message
                        )
                    }
                }
                .collect { fileResources ->
                    val initialUiModels = fileResources.map { FileUiModel(fileResource= it) }
                    _uiState.update {
                        (it as HomeUiState.Shown).copy(
                            files = initialUiModels
                        )
                    }

                    val bitmapJobs = fileResources.map { file ->
                        async { getBitMap(file) } // async로 감싸서 병렬 실행
                    }

                    val bitmaps = bitmapJobs.awaitAll()

                    val finalUiModels = fileResources.zip(bitmaps).map { (file, bitmap) ->
                        FileUiModel(
                            fileResource = file,
                            albumArt = bitmap,
                            isArtLoading = false
                        )
                    }

                    _uiState.update { (it as HomeUiState.Shown).copy(files = finalUiModels) }
                }
        }
    }

    fun toggleBookMark(fileResource: FileResource) {
        Timber.i("toggleBookMark() | fileResource: $fileResource")
        val currentState = _uiState.value
        if (currentState is HomeUiState.Shown) {
            val oldBookmarkedMusics = currentState.bookmarkedMusics
            val newBookMarkedMusics = if (fileResource in oldBookmarkedMusics) {
                oldBookmarkedMusics - fileResource
            } else {
                oldBookmarkedMusics + fileResource
            }
            _uiState.update { (it as HomeUiState.Shown).copy(bookmarkedMusics = newBookMarkedMusics) }
        }
    }

    private suspend fun getBitMap(fileResource: FileResource): Bitmap? {
        return withContext(Dispatchers.IO) {
            val url = "http://10.0.2.2:3000/uploads/${fileResource.originalName}"
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