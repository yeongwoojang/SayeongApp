package kr.co.fastcampus.sayeongapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kr.co.fastcampus.sayeongapp.domain.DemoUseCase
import kr.co.fastcampus.sayeongapp.dto.Demo
import javax.inject.Inject

@HiltViewModel
class MainVideModel @Inject constructor(
    private val demoUseCase: DemoUseCase
): ViewModel() {

    val ceh = CoroutineExceptionHandler { _, throwable ->
        Log.e("TEST_LOG", "Demo 조회 실패 :${throwable}")
    }
    private val _demo = MutableSharedFlow<Demo>()
    val demo = _demo.asSharedFlow()

    fun getDemoById(id: String) {
        viewModelScope.launch(ceh) {
            _demo.emit(demoUseCase(id).getOrThrow())

        }
    }
}