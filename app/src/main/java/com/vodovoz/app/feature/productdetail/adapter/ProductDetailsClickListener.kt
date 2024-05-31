package com.vodovoz.app.feature.productdetail.adapter

import android.content.Intent
import com.vodovoz.app.ui.model.ProductUI

interface ProductDetailsClickListener {

    //Header
    fun share(intent: Intent)
    fun backPress()
    fun navigateToReplacement(detailPicture: String, products: Array<ProductUI>, id: Long, name: String, categoryId: Long? = null)
    fun onTvCommentAmount(productId: Long)
    fun onYouTubeClick(videoCode: String)
    fun onDetailPictureClick(currentItem: Int, detailPictureList: Array<String>)

    fun showProductsByCategory(id: Long)
    fun showProductsByBrand(id: Long)

    fun onNextPageBrandProductsClick(position: Int)
    fun onNextPageMaybeLikeClick(position: Int)

    fun onQueryClick(query: String)

    fun onSendComment(id: Long)
    fun onShowAllComments(id: Long)

    fun onBlockButtonClick(productId: String, extProductId: String)

    fun onServiceClick(id: String)

}