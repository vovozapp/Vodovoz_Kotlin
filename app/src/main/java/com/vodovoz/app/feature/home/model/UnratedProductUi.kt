package com.vodovoz.app.feature.home.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.UnratedProductModel
import com.vodovoz.app.domain.general.model.UnratedProductsSectionModel


@Immutable
data class UnratedProductsSectionUi(
    val title: String,
    val productTitle: String,
    val countProductsText: String,
    val products: List<UnratedProductUi>,
) {
    companion object {
        val Empty: UnratedProductsSectionUi = UnratedProductsSectionUi("", "", "", emptyList())
    }
}

@Immutable
data class UnratedProductUi(
    val name: String,
    val id: Long,
    val detailPicture: String,
    val rating: Float = 0f
)

fun UnratedProductsSectionModel.toUi(): UnratedProductsSectionUi {
    return UnratedProductsSectionUi(
        title = title,
        productTitle = productTitle,
        countProductsText = countProductsText,
        products = products.map { it.toUi() }
    )
}

fun UnratedProductModel.toUi(): UnratedProductUi {
    return UnratedProductUi(
        name = name,
        id = id,
        detailPicture = detailPicture
    )
}
