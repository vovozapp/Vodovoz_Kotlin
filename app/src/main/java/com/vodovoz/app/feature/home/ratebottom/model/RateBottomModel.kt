package com.vodovoz.app.feature.home.ratebottom.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts

@JsonClass(generateAdapter = true)
data class RateBottomModel(
    val status: String?,
    val message: String?,
    @Json(name = "data")
    val rateBottomData: RateBottomData?,
)

@JsonClass(generateAdapter = true)
data class RateBottomData(
    @Json(name = "LISTRAZDEL")
    val productsList: List<ProductRateBottom>?,
    @Json(name = "TITLERAZDEL")
    val titleCategory: String?,
    @Json(name = "TITLETOVAR")
    val titleProduct: String?,
    @Json(name = "VSEGOTOVAR")
    val allProductsCount: String?
)

@JsonClass(generateAdapter = true)
data class ProductRateBottom(
    @Json(name = "DETAIL_PICTURE")
    val image: String?,
    @Json(name = "ID")
    val id: Int?,
    @Json(name = "NAME")
    val name: String?
)

data class CollapsedData(
    val title: String?,
    val body: String?,
    val imageList: List<CollapsedDataImage>?
)

data class CollapsedDataImage(
    val image: String?
): Item {

    override fun getItemViewType(): Int {
        return R.layout.item_collapsed_rate_image
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CollapsedDataImage) return false

        return this == item
    }

}