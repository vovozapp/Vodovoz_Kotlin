package com.vodovoz.app.feature.productdetail.adapter

import android.content.Intent
import com.vodovoz.app.ui.model.ProductUI

interface ProductDetailsClickListener {

    //Header
    fun share(intent: Intent)
    fun backPress()
    fun navigateToReplacement(detailPicture: String, products: Array<ProductUI>, id: Long, name: String)
    fun onTvCommentAmount(productId: Long)
    fun onYouTubeClick(videoCode: String)


    fun showFabBasket()
    fun showFabBell()
    fun showFabReplace()

    fun showProductsByCategory(id: Long)
    fun showProductsByBrand(id: Long)

    fun onNextPageBrandProductsClick()
    fun onNextPageMaybeLikeClick()

    fun onQueryClick(query: String)

    fun onSendComment(id: Long)
    fun onShowAllComments(id: Long)

}