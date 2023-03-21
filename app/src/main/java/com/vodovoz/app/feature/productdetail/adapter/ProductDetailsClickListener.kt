package com.vodovoz.app.feature.productdetail.adapter

import android.content.Intent
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI

interface ProductDetailsClickListener {

    //Header
    fun share(intent: Intent)
    fun backPress()
    fun navigateToReplacement(detailPicture: String, products: Array<ProductUI>, id: Long, name: String)


    fun showFabBasket()
    fun showFabBell()
    fun showFabReplace()

}