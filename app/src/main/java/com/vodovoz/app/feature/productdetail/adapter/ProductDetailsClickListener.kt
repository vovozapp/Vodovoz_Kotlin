package com.vodovoz.app.feature.productdetail.adapter

import android.content.Intent
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI

interface ProductDetailsClickListener {

    //Header
    fun share(intent: Intent)
    fun backPress()
    fun setScrollListener(height: Int)
    fun navigateToReplacement(detailPicture: String, products: Array<ProductUI>, id: Long, name: String)


    fun showFabBasket()
    fun showFabBell()
    fun showFabReplace()

    fun showFabPriceCond(boolean: Boolean, item: ProductDetailUI)
    fun showFabOldPrice(boolean: Boolean)
}