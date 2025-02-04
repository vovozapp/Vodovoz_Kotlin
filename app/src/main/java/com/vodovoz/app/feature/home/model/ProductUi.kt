package com.vodovoz.app.feature.home.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.ButtonInfo
import com.vodovoz.app.domain.general.model.ButtonModel
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

data class ButtonUi(
    val name: String,
    val action: ButtonInfo,
)

fun ButtonModel.mapToUi(): ButtonUi {
    return ButtonUi(
        name = name,
        action = action
    )
}

@Immutable
data class SectionUi<E>(
    val title: String,
    val items: List<E>,
    val button: ButtonUi?,
) {

    companion object {

        fun <T> empty() = SectionUi("", emptyList<T>(), null)

    }

}

fun <E, E2> SectionModel<E>.mapToUi(
    mapItems: (List<E>) -> List<E2>,
): SectionUi<E2> {
    return SectionUi(
        title = title,
        items = mapItems(items),
        button = button?.mapToUi()
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
) {}

fun List<LabelModel>.mapToUi(): List<LabelWithColorUi> {
    return mapNotNull { labelModel ->
        labelModel.mapToUi()
    }
}

fun LabelModel.mapToUi(): LabelWithColorUi? {
    return LabelWithColorUi(
        name,
        Color.fromHexOrNull(colorHex) ?: return null
    )
}
