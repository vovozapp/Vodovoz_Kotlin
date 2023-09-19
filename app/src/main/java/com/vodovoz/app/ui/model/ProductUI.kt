package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPicturePager
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductUI(
    val id: Long,
    val name: String,
    var detailPicture: String,
    var isFavorite: Boolean,
    var leftItems: Int,
    val pricePerUnit: Int,
    val priceList: List<PriceUI>,
    val status: String,
    val statusColor: String,
    var rating: Float,
    val isBottle: Boolean,
    val isGift: Boolean,
    var isAvailable: Boolean,
    val commentAmount: String,
    var cartQuantity: Int = 0,
    var orderQuantity: Int = 0,
    val depositPrice: Int = 0,
    val detailPictureList: List<String> = listOf(),
    val replacementProductUIList: List<ProductUI> = listOf(),
    var oldQuantity: Int = 0,
    var linear: Boolean = true,
    val pricePerUnitStringBuilder: String = "",
    val currentPriceStringBuilder: String = "",
    val oldPriceStringBuilder: String = "",
    val minimalPriceStringBuilder: String = "",
    val haveDiscount: Boolean = false,
    val priceConditionStringBuilder: String = "",
    val discountPercentStringBuilder: String = "",
    val detailPictureListPager: List<DetailPicturePager> = emptyList(),
    val serviceDetailCoef: Int? = null,
    val serviceGiftId: String? = null,
    val chipsBan: Int? = null,
    val totalDisc: Double = 0.0
) : Parcelable, Item {

    companion object {
        const val PRODUCT_VIEW_TYPE = -500
        const val PRODUCT_VIEW_TYPE_GRID = -600
    }

    override fun getItemViewType(): Int {
        return if (linear) PRODUCT_VIEW_TYPE else PRODUCT_VIEW_TYPE_GRID
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProductUI) return false

        return id == item.id
    }
}

