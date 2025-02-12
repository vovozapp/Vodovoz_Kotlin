package com.vodovoz.app.feature.about_product.model

import com.vodovoz.app.design_system.model.DocumentUi

sealed class AboutProductEvent {
    data class GoToDocumentViewer(val document: DocumentUi) : AboutProductEvent() {

    }

    data object GoBack: AboutProductEvent()

}