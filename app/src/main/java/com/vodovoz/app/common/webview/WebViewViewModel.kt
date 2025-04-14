package com.vodovoz.app.common.webview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.webview.model.WebViewEvents
import com.vodovoz.app.common.webview.model.WebViewState
import com.vodovoz.app.common.webview.model.WebViewUiState
import com.vodovoz.app.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel  @Inject constructor(
    savedStateHandle: SavedStateHandle
) : MviViewModel<WebViewState, WebViewEvents>(WebViewState()) {

    private val title = savedStateHandle.get<String>("title") ?: navigateBack().run { "" }
    private val url = savedStateHandle.get<String>("url") ?: navigateBack().run { "" }

    init {
        setInitialData(title, url)
    }

    private fun setInitialData(title: String, url: String) = viewModelScope.launch {
        _state.update { s -> s.copy(title = title, url = url) }
    }

    fun setUiState(uiState: WebViewUiState) = viewModelScope.launch {
        _state.update { s -> s.copy(uiState = uiState) }
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(WebViewEvents.GoBack)
    }

}