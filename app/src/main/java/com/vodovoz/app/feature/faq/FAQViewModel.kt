package com.vodovoz.app.feature.faq

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.feature.buy_certificate.model.FAQItemUi
import com.vodovoz.app.feature.buy_certificate.model.FAQUi
import com.vodovoz.app.feature.faq.model.FAQEvent
import com.vodovoz.app.feature.faq.model.FAQState
import com.vodovoz.app.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FAQViewModel @Inject constructor(savedStateHandle: SavedStateHandle) :
    MviViewModel<FAQState, FAQEvent>(FAQState()) {

    private val faq = savedStateHandle.get<FAQUi>("faq") ?: FAQUi.Empty.also {
        navigateBack()
    }

    init {
        initFAQ(faq)
    }

    private fun initFAQ(faq: FAQUi) = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                name = faq.name,
                items = faq.items
            )
        }
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(FAQEvent.GoBack)
    }

    fun changeExpand(faqItem: FAQItemUi) = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                items = s.items.map {
                    if (faqItem.name == it.name) it.copy(expanded = !it.expanded) else it
                }
            )
        }
    }


}