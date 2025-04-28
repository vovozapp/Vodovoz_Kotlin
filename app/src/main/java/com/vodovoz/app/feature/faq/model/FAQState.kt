package com.vodovoz.app.feature.faq.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.feature.buy_certificate.model.FAQItemUi

@Immutable
data class FAQState(
    val name: String = "",
    val items: List<FAQItemUi> = emptyList(),
)
