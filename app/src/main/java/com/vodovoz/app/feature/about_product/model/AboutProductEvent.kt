package com.vodovoz.app.feature.about_product.model

sealed class AboutProductEvent {

    data object GoBack: AboutProductEvent()

}