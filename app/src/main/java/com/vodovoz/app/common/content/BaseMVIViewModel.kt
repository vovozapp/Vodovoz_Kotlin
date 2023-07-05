package com.vodovoz.app.common.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseMVIViewModel<S : State, E: Event>(
    idleState: S
) : ViewModel() {

    protected val uiStateListener = MutableStateFlow(PagingState.idle(idleState))
    protected val state get() = uiStateListener.value

    protected val eventsListener = MutableSharedFlow<E>()

    fun observeUiState() = uiStateListener.asStateFlow()
    fun observeEvents() = eventsListener.asSharedFlow()

    fun emitEvent(event: E) {
        viewModelScope.launch {
            eventsListener.emit(event)
        }
    }
}