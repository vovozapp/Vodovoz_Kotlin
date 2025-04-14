package com.vodovoz.app.ui.base

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.util.SplashFileConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashFileViewModel @Inject constructor(
    private val appContext: Application,
) : ViewModel() {

    private val _fileIsLoading = MutableStateFlow(true)
    val fileLoading = _fileIsLoading.asStateFlow()

    fun downloadSplashFile() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                SplashFileConfig.downloadSplashFile(appContext)
            }.onFailure {
                _fileIsLoading.update { false }
            }
        }
    }

    fun finishFileLoading() = viewModelScope.launch {
        _fileIsLoading.update { false }
    }
}