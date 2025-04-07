package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.vodovoz.app.domain.general.model.ButtonAction
import com.vodovoz.app.domain.general.model.ButtonModel
import com.vodovoz.app.domain.general.model.CategoryWithProductsModel
import com.vodovoz.app.domain.general.model.LabelModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.SectionModel
import com.vodovoz.app.util.fromHexOrNull
import com.yandex.mapkit.search.Advertisement.Product


@JvmName("withUpdatedFavoritesSectionProduct")
fun SectionUi<ProductUi>.withUpdatedFavorites(favorites: Map<Long, Boolean>): SectionUi<ProductUi> {
    return copy(
        items = items.withUpdatedFavorites(favorites)
    )
}

fun SectionUi<CategoryWithProductsUi>.withUpdatedFavorites(favorites: Map<Long, Boolean>): SectionUi<CategoryWithProductsUi> {
    return copy(
        items = items.withUpdatedFavorites(favorites)
    )
}

@JvmName("withUpdatedFavoritesCategoriesWithProducts")
fun List<CategoryWithProductsUi>.withUpdatedFavorites(favorites: Map<Long, Boolean>): List<CategoryWithProductsUi> {
    return map{ categoryWithProductsUi ->
        categoryWithProductsUi.withUpdatedFavorites(favorites)
    }
}

fun CategoryWithProductsUi.withUpdatedFavorites(favorites: Map<Long, Boolean>): CategoryWithProductsUi {
    return copy(products = products.withUpdatedFavorites(favorites))
}

fun List<ProductUi>.withUpdatedFavorites(favorites: Map<Long, Boolean>): List<ProductUi> {
    return map { product ->
        product.copy(isFavorite = favorites[product.id] ?: product.isFavorite)
    }
}

@JvmName("withUpdatedCartSectionProduct")
fun SectionUi<ProductUi>.withUpdatedCart(cart: Map<Long, Int>): SectionUi<ProductUi> {
    return copy(
        items = items.withUpdatedCart(cart)
    )
}

@JvmName("withUpdatedCartSectionCategory")
fun SectionUi<CategoryWithProductsUi>.withUpdatedCart(cart: Map<Long, Int>): SectionUi<CategoryWithProductsUi> {
    return copy(
        items = items.withUpdatedCart(cart)
    )
}

@JvmName("withUpdatedCartCategoriesWithProducts")
fun List<CategoryWithProductsUi>.withUpdatedCart(cart: Map<Long, Int>): List<CategoryWithProductsUi> {
    return map { categoryWithProductsUi ->
        categoryWithProductsUi.withUpdatedCart(cart)
    }
}

@JvmName("withUpdatedCartCategoryWithProducts")
fun CategoryWithProductsUi.withUpdatedCart(cart: Map<Long, Int>): CategoryWithProductsUi {
    return copy(products = products.withUpdatedCart(cart))
}

@JvmName("withUpdatedCartProductList")
fun List<ProductUi>.withUpdatedCart(cart: Map<Long, Int>): List<ProductUi> {
    return map { product ->
        product.copy(cartQuantity = cart[product.id] ?: product.cartQuantity)
    }
}

@JvmName("withUpdatedLoadingProductList")
fun List<ProductUi>.withUpdatedLoading(blockedProductsIds: Set<Long>): List<ProductUi> {
    return map { product ->
        product.copy(cartLoading = product.id in blockedProductsIds)
    }
}

@JvmName("withUpdatedLoadingCategoryWithProducts")
fun CategoryWithProductsUi.withUpdatedLoading(blockedProductsIds: Set<Long>): CategoryWithProductsUi {
    return copy(products = products.withUpdatedLoading(blockedProductsIds))
}

@JvmName("withUpdatedLoadingCategoriesWithProducts")
fun List<CategoryWithProductsUi>.withUpdatedLoading(blockedProductsIds: Set<Long>): List<CategoryWithProductsUi> {
    return map { it.withUpdatedLoading(blockedProductsIds) }
}

@JvmName("withUpdatedLoadingSectionProduct")
fun SectionUi<ProductUi>.withUpdatedLoading(blockedProductsIds: Set<Long>): SectionUi<ProductUi> {
    return copy(items = items.withUpdatedLoading(blockedProductsIds))
}

@JvmName("withUpdatedLoadingSectionCategory")
fun SectionUi<CategoryWithProductsUi>.withUpdatedLoading(blockedProductsIds: Set<Long>): SectionUi<CategoryWithProductsUi> {
    return copy(items = items.withUpdatedLoading(blockedProductsIds))
}


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


fun CategoryWithProductsModel.toUi(): CategoryWithProductsUi {
    return CategoryWithProductsUi(
        id = id,
        name = name,
        products = products.map { it.toUi() }
    )
}

data class ButtonUi(
    val name: String,
    val action: ButtonAction,
) {
    companion object {
        val Empty = ButtonUi("", ButtonAction.Id(-1))
    }
}

fun ButtonModel.toUi(): ButtonUi {
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

fun <E, E2> SectionModel<E>.toUi(
    mapItems: (List<E>) -> List<E2>,
): SectionUi<E2> {
    return SectionUi(
        title = title,
        items = mapItems(items),
        button = button?.toUi()
    )
}

fun SectionModel<ProductModel>.toUi(): SectionUi<ProductUi> {
    return SectionUi(
        title = title,
        items = items.mapToUi(),
        button = button?.toUi()
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
    val pricePerUnit: Int?,
    val unitOfMeasurement: String?
)

fun List<ProductModel>.mapToUi(): List<ProductUi>{
    return mapNotNull { it.toUi() }
}

fun ProductModel.toUi(): ProductUi {
    return ProductUi(
        id = id,
        isFavorite = isFavorite,
        rating = rating,
        price = firstPrice.price,
        oldPrice = firstPrice.oldPrice,
        name = name,
        cartQuantity = cartQuantity,
        cartLoading = false,
        image = picture,
        labels = labels.toUi(),
        isAvailable = quantity > 0,
        pricePerUnit = pricePerUnit,
        unitOfMeasurement = unitOfMeasurement
    )
}


@Immutable
data class LabelWithColorUi(
    val name: String,
    val color: Color,
)

fun List<LabelModel>.toUi(): List<LabelWithColorUi> {
    return mapNotNull { labelModel ->
        labelModel.toUi()
    }
}

fun LabelModel.toUi(): LabelWithColorUi? {
    return LabelWithColorUi(
        name,
        Color.fromHexOrNull(colorHex) ?: return null
    )
}
