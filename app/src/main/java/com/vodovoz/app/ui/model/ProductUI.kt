package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
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
    val rating: Double,
    val isBottle: Boolean,
    val isGift: Boolean,
    var isAvailable: Boolean,
    val commentAmount: String,
    var cartQuantity: Int = 0,
    var orderQuantity: Int = 0,
    val depositPrice: Int = 0,
    val detailPictureList: List<String> = listOf(),
    val replacementProductUIList: List<ProductUI> = listOf()
) : Parcelable, Item {

    companion object {
        const val PRODUCT_VIEW_TYPE = -500
    }

    override fun getItemViewType(): Int {
        return PRODUCT_VIEW_TYPE
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProductUI) return false

        return id == item.id
    }
}

