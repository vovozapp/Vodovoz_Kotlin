package com.vodovoz.app.data.model.common

import java.io.Serializable

sealed class BannerActionEntity(
    val action: String?,
    val actionColor: String?
) : Serializable {
    class Brand(
        val brandId: Long,
        action: String? = null,
        actionColor: String? = null
    ) : BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
    class Brands(
        val brandIdList: List<Long>,
        action: String? = null,
        actionColor: String? = null
    ) : BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
    class Promotion(
        val promotionId: Long,
        action: String? = null,
        actionColor: String? = null
    ) : BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
    class Promotions(
        val categoryId: Long,
        action: String? = null,
        actionColor: String? = null
    ) : BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
    class AllPromotions(
        action: String? = null,
        actionColor: String? = null
    ) : BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
    class Product(
        val productId: Long,
        action: String? = null,
        actionColor: String? = null
    ) : BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
    class Products(
        val categoryId: Long,
        action: String? = null,
        actionColor: String? = null
    ) : BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
    class Link(
        val url: String,
        action: String? = null,
        actionColor: String? = null
    ) : BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
    class Category(
        val categoryId: Long,
        action: String? = null,
        actionColor: String? = null
    ): BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
    class Discount(
        action: String? = null,
        actionColor: String? = null
    ) : BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
    class Novelties(
        action: String? = null,
        actionColor: String? = null
    ) : BannerActionEntity(
        action = action,
        actionColor = actionColor
    )
}