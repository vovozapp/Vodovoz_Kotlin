package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.order.OrderProductModel
import com.vodovoz.app.domain.general.model.order.OrderProductPresentModel
import com.vodovoz.app.feature.cart.model.ProductRestrictionUi
import com.vodovoz.app.util.fromHexOrUnspecified

@Immutable
data class OrderProductUi(
    val id: Long,
    val name: String,
    val quantity: Int,
    val articleNumberText: String,
    val depositText: String?,
    val price: PriceUi?,
    val isShowcaseProduct: Boolean,
    val image: String,
    val labels: List<LabelUi>,
    val pricePerUnit: Int?,
    val unitOfMeasurement: String?,
    val catalogQuantity: Int,
    val isFavorite: Boolean,
    val present: OrderProductPresentUi?,
    val restrictions: ProductRestrictionUi,
)

@Immutable
data class OrderProductPresentUi(
    val title: String,
    val color: Color,
)


fun List<OrderProductModel>.mapToUi(): List<OrderProductUi> {
    return map { it.toUi() }
}

fun OrderProductModel.toUi(): OrderProductUi {
    return OrderProductUi(
        id = id,
        name = name,
        quantity = quantity,
        articleNumberText = articleNumberText,
        depositText = depositText,
        price = price?.toUi(),
        isShowcaseProduct = isShowcaseProduct,
        image = image,
        labels = labels.map { it.toUi() },
        pricePerUnit = pricePerUnit,
        unitOfMeasurement = unitOfMeasurement,
        catalogQuantity = catalogQuantity,
        isFavorite = isFavorite,
        present = present?.toUi(),
        restrictions = ProductRestrictionUi.fromCode(restrictionCode)
    )
}

fun OrderProductPresentModel.toUi(): OrderProductPresentUi {
    return OrderProductPresentUi(
        title, Color.fromHexOrUnspecified(color)
    )
}
