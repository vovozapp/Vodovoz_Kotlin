package com.vodovoz.app.feature.home.viewholders.homeproducts

interface ProductsShowAllListener {
    fun showAllDiscountProducts(id: Long)
    fun showAllTopProducts(id: Long)
    fun showAllNoveltiesProducts(id: Long)
    fun showAllBottomProducts(id: Long)
}