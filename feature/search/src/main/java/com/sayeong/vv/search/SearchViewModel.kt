package com.sayeong.vv.search

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayeong.vv.domain.GetAlbumArtUseCase
import com.sayeong.vv.domain.SearchMusicUseCase
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.ui.MusicUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMusicUseCase: SearchMusicUseCase,
    private val getAlbumArtUseCase: GetAlbumArtUseCase
) : ViewModel() {

    private val bookmarkedMusics = MutableStateFlow<Set<MusicResource>>(emptySet())

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val musicResources: SharedFlow<List<MusicResource>> = query.flatMapLatest { queryText ->
        if (queryText.isNotEmpty()) {
            searchMusicUseCase(queryText)
        } else {
            flowOf(emptyList())
        }
    }.catch {
        emit(emptyList())
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    private val albumArtCache: StateFlow<Map<String, Bitmap?>> =
        musicResources.scan(emptyMap<String, Bitmap?>()) { cachedMap, musicResources ->
            val fetchToAlbumArts = musicResources.filter { musicResource ->
                !cachedMap.containsKey(musicResource.originalName)
            }
            if (fetchToAlbumArts.isEmpty()) {
                cachedMap
            } else {
                val newAlbumMap = withContext(Dispatchers.IO) {
                    musicResources.map {
                        async { it.originalName to getBitMap(it.originalName) }
                    }.awaitAll().toMap()
                }

                cachedMap + newAlbumMap
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val searchUiState: StateFlow<SearchUiState> = combine(
        musicResources,
        albumArtCache,
        bookmarkedMusics
    ) { musicResources, albumArtCache, bookmarkedMusics ->
        val searchUiModel = musicResources.map {
            MusicUiModel(
                musicResource = it,
                albumArt = albumArtCache[it.originalName],
                isArtLoading = !albumArtCache.containsKey(it.originalName)
            )
        }
        SearchUiState.Shown(
            musicResources = searchUiModel,
            bookmarkedMusics = bookmarkedMusics
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState.Loading
    )

    fun search(query: String) {
        Timber.i("search() | query: $query")
        _query.value = query
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