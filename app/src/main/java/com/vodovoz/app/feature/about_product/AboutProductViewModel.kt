package com.vodovoz.app.feature.about_product

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.about_product.AboutProductManager
import com.vodovoz.app.design_system.model.DocumentUi
import com.vodovoz.app.feature.about_product.model.AboutProductEvent
import com.vodovoz.app.feature.about_product.model.AboutProductState
import com.vodovoz.app.feature.document_viewer.model.DocumentViewerEvent
import com.vodovoz.app.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutProductViewModel @Inject constructor(
    private val aboutProductManager: AboutProductManager,
) : MviViewModel<AboutProductState, AboutProductEvent>(AboutProductState()) {


    fun loadAboutProductInfo() = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                tabs = aboutProductManager.tabs,
                characteristics = aboutProductManager.characteristicsBlock,
                description = aboutProductManager.descriptionBlock,
                documents = aboutProductManager.documentsBlock
            )
        }
    }

    fun selectTab(i: Int) = viewModelScope.launch {
        _state.update { s ->
            s.copy(selectedTabIndex = i)
        }
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(AboutProductEvent.GoBack)
    }

    fun navigateToDocumentViewer(document: DocumentUi) = viewModelScope.launch {
        _events.emit(AboutProductEvent.GoToDocumentViewer(document))
    }

}