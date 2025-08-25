package com.sayeong.vv.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayeong.vv.domain.SearchMusicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.onStart
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
    fun search(query: String) {
        Timber.i("search() | query: $query")
        viewModelScope.launch(ceh) {
            searchMusicUseCase(query)
                .onStart {

                }
                .collect{

                }
        }
    }
}