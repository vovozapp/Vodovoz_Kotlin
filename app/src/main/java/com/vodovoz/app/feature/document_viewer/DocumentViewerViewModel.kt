package com.vodovoz.app.feature.document_viewer

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.design_system.model.DocumentUi
import com.vodovoz.app.feature.document_viewer.model.DocumentViewerEvent
import com.vodovoz.app.feature.document_viewer.model.DocumentViewerState
import com.vodovoz.app.feature.document_viewer.model.DocumentViewerUiState
import com.vodovoz.app.ui.mvi.MviViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentViewerViewModel :
    MviViewModel<DocumentViewerState, DocumentViewerEvent>(DocumentViewerState()) {

    fun setDocument(document: DocumentUi) {
        _state.update { s ->
            s.copy(
                currentDocument = document
            )
        }
    }

    fun setUiState(uiState: DocumentViewerUiState.Success) = viewModelScope.launch {
        _state.update { s ->
            s.copy(uiState = uiState)
        }
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(DocumentViewerEvent.GoBack)
    }

}