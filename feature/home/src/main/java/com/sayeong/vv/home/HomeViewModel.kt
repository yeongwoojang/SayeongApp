package com.sayeong.vv.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayeong.vv.domain.GetFileListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFileListUseCase: GetFileListUseCase
): ViewModel() {

    init {
        getFileList()
    }

    private fun getFileList() {
        viewModelScope.launch {
            val result = getFileListUseCase()
        }
    }

}