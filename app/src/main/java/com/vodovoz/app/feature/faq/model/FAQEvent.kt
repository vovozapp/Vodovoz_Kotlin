package com.vodovoz.app.feature.faq.model

sealed class FAQEvent {

    data object GoBack: FAQEvent()

}