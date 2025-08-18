package com.sayeong.vv.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayeong.vv.domain.GetTopicsUseCase
import com.sayeong.vv.model.TopicResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val topics: List<TopicResource> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase,
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getTopics()
    }

    private fun getTopics() {
        viewModelScope.launch {
            getTopicsUseCase()
                .onStart {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
                .catch { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
                .collect { topics ->
                    _uiState.update { it.copy(isLoading = false, topics = topics) }
                }
        }
    }
}