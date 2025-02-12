package com.vodovoz.app.feature.document_viewer.model

import com.vodovoz.app.design_system.model.DocumentUi
import com.vodovoz.app.feature.about_product.model.AboutProductEvent

sealed class DocumentViewerEvent {
    data object GoBack : DocumentViewerEvent()
}