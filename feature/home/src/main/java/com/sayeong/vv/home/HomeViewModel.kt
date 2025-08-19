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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase,
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
                        _uiState.value = HomeUiState.NotShown
                    }
                }
        }
    }

    fun onTopicClick(topicId: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Shown) {
            val oldSelectedIds = currentState.selectedTopicIds
            val newSelectedIds = if (oldSelectedIds.contains(topicId)) {
                oldSelectedIds - topicId
            } else {
                oldSelectedIds + topicId
            }

            _uiState.update { currentState.copy(selectedTopicIds = newSelectedIds) }
        }
    }

    fun onDoneClick() {
        //TODO TopSelect 영역을 제거 하기 위한 로직 필요
    }
}