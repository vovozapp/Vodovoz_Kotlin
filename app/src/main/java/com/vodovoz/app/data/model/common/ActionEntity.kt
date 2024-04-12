package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


sealed class ActionEntity(
    open val action: String?,
    open val actionColor: String?,
) : Parcelable {

    @Parcelize
    class Brand(
        val brandId: Long,
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Brands(
        val brandIdList: List<Long>,
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Promotion(
        val promotionId: Long,
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Promotions(
        val categoryId: Long,
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class AllPromotions(
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Product(
        val productId: Long,
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Products(
        val categoryId: Long,
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Link(
        val url: String,
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Category(
        val categoryId: Long,
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Discount(
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Novelties(
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class WaterApp(
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Delivery(
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class Profile(
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )

    @Parcelize
    class BuyCertificate(
        override val action: String? = null,
        override val actionColor: String? = null,
    ) : ActionEntity(
        action = action,
        actionColor = actionColor
    )
}