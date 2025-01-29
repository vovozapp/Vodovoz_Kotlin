package com.vodovoz.app.ui.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.data.model.common.LabelEntity

@Immutable
data class ProductDetailUI(
    val id: Long = 0,
    val name: String = "",
    val previewText: String = "",
    val shareUrl: String = "",
    val detailText: String = "",
    var isFavorite: Boolean = false,
    val leftItems: Int = 0,
    val youtubeVideoCode: String = "",
    val rutubeVideoCode: String = "",
    var rating: String = "0",
    val isAvailable: Boolean = false,
    val status: String = "",
    val statusColor: String = "",
    val labels: List<LabelEntity> = emptyList(),
    val consumerInfo: String = "",
    val pricePerUnit: String = "",
    val commentsAmount: Int = 0,
    val commentsAmountText: String = "",
    val brandUI: BrandUI? = null,
    var cartQuantity: Int = 0,
    val propertiesGroupUIList: List<PropertiesGroupUI> = listOf(),
    val priceUIList: List<PriceUI> = listOf(),
    val detailPictureList: List<String> = listOf(),
    var oldQuantity: Int = 0,
    val blockList: List<BlockUI> = listOf(),
)

fun ProductDetailUI.getDeposit(): Int {
    return propertiesGroupUIList.firstOrNull { groupUI ->
        groupUI.propertyUIList.firstOrNull {
            it.name.contains(
                "стоимость",
                true
            )
        } != null
    }?.propertyUIList?.firstOrNull { propertyUI ->
        propertyUI.name.contains(
            "стоимость",
            true
        )
    }?.value?.takeWhile { s -> s.digitToIntOrNull() != null }?.toIntOrNull() ?: 0
}


fun ProductDetailUI.getArticleNumber(): String {
    return propertiesGroupUIList.firstOrNull()?.propertyUIList?.firstOrNull { propertyUI ->
        propertyUI.name.contains(
            "артикул",
            true
        )
    }?.value ?: ""
}

