package com.sayeong.vv.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayeong.vv.domain.SearchMusicUseCase
import com.sayeong.vv.ui.MusicUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMusicUseCase: SearchMusicUseCase
): ViewModel() {

    val ceh = CoroutineExceptionHandler { coroutineContext, throwable ->
        Timber.i("Search() | throwable: $throwable")
    }

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    val searchUiState: StateFlow<SearchUiState> = query.flatMapLatest { queryText ->
         searchMusicUseCase(queryText).map {
             val searchUiModel = it.map { musicResource ->
                MusicUiModel(
                    musicResource = musicResource,
                    albumArt = null,
                    isArtLoading = true
                )
            }
             SearchUiState.Shown(
                 musicResources = searchUiModel
             )
        }.catch { throwable ->
            SearchUiState.Error(throwable.message)
         }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState.Loading
    )

    fun search(query: String) {
        Timber.i("search() | query: $query")
        _query.value = query
    }
}