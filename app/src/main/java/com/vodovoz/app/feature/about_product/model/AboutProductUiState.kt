package com.vodovoz.app.feature.about_product.model

sealed interface AboutProductUiState {

    data object Loading: AboutProductUiState
    data object Success: AboutProductUiState

}