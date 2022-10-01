package com.vodovoz.app.ui.model.custom

import com.vodovoz.app.ui.model.*

class ProductDetailBundleUI(
    val productDetailUI: ProductDetailUI,
    val serviceUIList: List<ServiceUI> = listOf(),
    val categoryUI: CategoryUI,
    val commentUIList: List<CommentUI> = listOf(),
    val searchWordList: List<String> = listOf(),
    val maybeLikeProductUIList: List<ProductUI> = listOf(),
    val promotionUIList: List<PromotionUI> = listOf(),
    val recommendProductUIList: List<ProductUI> = listOf(),
    val buyWithProductUIList: List<ProductUI> = listOf(),
    val replacementProductsCategoryDetail: CategoryDetailUI?
)