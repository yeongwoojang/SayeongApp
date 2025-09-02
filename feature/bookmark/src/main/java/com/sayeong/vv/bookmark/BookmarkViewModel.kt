package com.sayeong.vv.bookmark

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayeong.vv.domain.GetAlbumArtUseCase
import com.sayeong.vv.domain.GetBookmarkedMusicUseCase
import com.sayeong.vv.domain.InsertBookmarkedUseCase
import com.sayeong.vv.domain.RemoveBookmarkedUseCase
import com.sayeong.vv.domain.SearchMusicUseCase
import com.sayeong.vv.model.MusicResource
import com.sayeong.vv.ui.MusicUiModel
import com.sayeong.vv.ui.utils.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getAlbumArtUseCase: GetAlbumArtUseCase,
    private val getBookmarkedMusicUseCase: GetBookmarkedMusicUseCase,
    private val insertBookmarkedUseCase: InsertBookmarkedUseCase,
    private val removeBookmarkedUseCase: RemoveBookmarkedUseCase,
) : ViewModel() {

    private val bookmarkedMusics: StateFlow<Set<MusicResource>> = getBookmarkedMusicUseCase().map {
        it.toSet()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    private val albumArtCache: StateFlow<Map<String, Bitmap?>> =
        bookmarkedMusics.scan(emptyMap<String, Bitmap?>()) { cachedMap, musicResources ->
            val fetchToAlbumArts = musicResources.filter { musicResource ->
                !cachedMap.containsKey(musicResource.originalName)
            }
            if (fetchToAlbumArts.isEmpty()) {
                cachedMap
            } else {
                val newAlbumMap = withContext(Dispatchers.IO) {
                    musicResources.map {
                        async { it.originalName to getAlbumArtUseCase(it.originalName).toBitmap() }
                    }.awaitAll().toMap()
                }

                cachedMap + newAlbumMap
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val bookmarkUiState: StateFlow<BookmarkUIState> = combine(
        albumArtCache,
        bookmarkedMusics
    ) { albumArtCache, bookmarkedMusics ->
        val searchUiModel = bookmarkedMusics.map {
            MusicUiModel(
                musicResource = it,
                albumArt = albumArtCache[it.originalName],
                isArtLoading = !albumArtCache.containsKey(it.originalName)
            )
        }
        BookmarkUIState.Shown(
            musicResources = searchUiModel,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BookmarkUIState.Loading
    )

    fun toggleBookMark(musicResource: MusicResource) {
        Timber.i("toggleBookMark() | musicResource: $musicResource")
        viewModelScope.launch {
            if (musicResource in bookmarkedMusics.value) {
                removeBookmarkedUseCase(musicResource)
            } else {
                insertBookmarkedUseCase(musicResource)
            }
        }
    }
}