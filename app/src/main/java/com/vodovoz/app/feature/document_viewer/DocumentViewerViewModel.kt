package com.vodovoz.app.feature.document_viewer

import com.vodovoz.app.feature.document_viewer.model.DocumentViewerEvent
import com.vodovoz.app.feature.document_viewer.model.DocumentViewerState
import com.vodovoz.app.ui.mvi.MviViewModel

class DocumentViewerViewModel :
    MviViewModel<DocumentViewerState, DocumentViewerEvent>(DocumentViewerState()) {

}