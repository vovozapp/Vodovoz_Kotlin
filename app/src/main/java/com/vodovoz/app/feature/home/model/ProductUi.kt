package com.vodovoz.app.feature.home.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.CategoryWithProductsModel
import com.vodovoz.app.domain.general.model.LabelModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.SectionModel
import com.vodovoz.app.util.fromHexOrNull

@Immutable
data class CategoryWithProductsUi(
    val id: Long,
    val name: String,
    val products: List<ProductUi>,
) {

    companion object {
        val Empty = CategoryWithProductsUi(-1, "", emptyList())
    }

}


fun CategoryWithProductsModel.mapToUi(): CategoryWithProductsUi {
    return CategoryWithProductsUi(
        id = id,
        name = name,
        products = products.map { it.mapToUi() }
    )
}

@Immutable
data class SectionUi(
    val name: String,
    val categoryWithProductsList: List<CategoryWithProductsUi>,
    val showAllId: Int?,
) {

    companion object {
        val Empty = SectionUi("", emptyList(), -1)
    }

}

fun SectionModel.mapToUi(): SectionUi {
    return SectionUi(
        name = name ?: "",
        categoryWithProductsList = categoryWithProductsList.map { it.mapToUi() },
        showAllId = showAllId
    )
}


@Immutable
data class ProductUi(
    val id: Long,
    val isFavorite: Boolean,
    val rating: Float,
    val price: Float,
    val oldPrice: Float,
    val name: String,
    val cartQuantity: Int,
    val cartLoading: Boolean,
    val image: String,
    val labels: List<LabelWithColorUi>,
    val isAvailable: Boolean,
)

fun ProductModel.mapToUi(): ProductUi {
    return ProductUi(
        id = id,
        isFavorite = isFavorite,
        rating = rating,
        price = firstPrice.price,
        oldPrice = firstPrice.oldPrice,
        name = name,
        cartQuantity = -1,
        cartLoading = false,
        image = picture,
        labels = labels.mapToUi(),
        isAvailable = quantity > 0,
    )
}


@Immutable
data class LabelWithColorUi(
    val name: String,
    val color: Color,
)

fun List<LabelModel>.mapToUi(): List<LabelWithColorUi> {
    return mapNotNull { labelModel ->
        LabelWithColorUi(
            labelModel.name,
            Color.fromHexOrNull(labelModel.colorHex) ?: return@mapNotNull null
        )
    }
}
