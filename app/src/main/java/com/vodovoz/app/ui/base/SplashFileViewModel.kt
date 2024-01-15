package com.vodovoz.app.ui.base

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.util.SplashFileConfig
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashFileViewModel @Inject constructor(
    private val appContext: Application,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        downloadSplashFile()
    }

    fun downloadSplashFile() {
        if (SplashFileConfig.getSplashFile(appContext).exists()) {
            _isLoading.value = false
        }
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                SplashFileConfig.downloadSplashFile(appContext)
            }.onFailure {
                debugLog { it.message.toString() }
            }
            _isLoading.value = false
        }
    }
}