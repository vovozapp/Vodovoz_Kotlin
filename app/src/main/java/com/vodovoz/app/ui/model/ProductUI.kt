package com.vodovoz.app.ui.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.model.common.LabelEntity
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPicturePager
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class ProductUI(
    val id: Long,
    val name: String,
    var detailPicture: String,
    var isFavorite: Boolean,
    var leftItems: Int,
    val pricePerUnit: String,
    val priceList: List<PriceUI>,
    val status: String,
    val statusColor: String,
    val labels: List<LabelEntity> = emptyList(),
    var rating: Float,
    val isBottle: Boolean,
    val isGift: Boolean,
    var isAvailable: Boolean,
    var canBuy: Boolean,
    val commentAmount: String,
    var cartQuantity: Int = 0,
    var orderQuantity: Int = 0,
    val depositPrice: Int = 0,
    val detailPictureList: List<String> = listOf(),
    val replacementProductUIList: List<ProductUI> = listOf(),
    var oldQuantity: Int = 0,
    var linear: Boolean = true,
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
    val totalDisc: Double = 0.0,
    val conditionPrice: String = "",
    val condition: String = "",
    val forCart: Boolean = false,
    val giftText: String  = "",
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

