package com.vodovoz.app.feature.document_viewer.model

import com.vodovoz.app.design_system.model.DocumentUi

data class DocumentViewerState(
    val currentDocument: DocumentUi = DocumentUi("", 0f, "", "", "", ""),
    val uiState: DocumentViewerUiState = DocumentViewerUiState.Loading,
)

sealed interface DocumentViewerUiState {
    data object Loading : DocumentViewerUiState
    data object Success : DocumentViewerUiState
}