package com.vodovoz.app.ui.model.custom

import com.vodovoz.app.feature.all.comments.model.CommentImage
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.ServiceUI

class ProductDetailBundleUI(
    val productDetailUI: ProductDetailUI,
    val serviceUIList: List<ServiceUI> = listOf(),
    val categoryUI: CategoryUI,
    val commentUIList: List<CommentUI> = listOf(),
    val commentImages: List<CommentImage> = listOf(),
    val searchWordList: List<String> = listOf(),
    val maybeLikeProductUIList: List<ProductUI> = listOf(),
    val promotionsAction: PromotionsActionUI? = null,
    val recommendProductUIList: List<ProductUI> = listOf(),
    val buyWithProductUIList: List<ProductUI> = listOf(),
    val replacementProductsCategoryDetail: CategoryDetailUI?
)