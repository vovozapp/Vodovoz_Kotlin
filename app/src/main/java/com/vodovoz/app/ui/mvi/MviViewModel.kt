package com.vodovoz.app.ui.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

open class MviViewModel<STATE, EVENT>(state: STATE): ViewModel() {

    protected val _state = MutableStateFlow(state)
    val state = _state.asStateFlow()

    protected val _events = MutableSharedFlow<EVENT>()
    val events = _events.asSharedFlow()


}