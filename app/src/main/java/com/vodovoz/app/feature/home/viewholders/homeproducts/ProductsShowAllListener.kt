package com.vodovoz.app.feature.home.viewholders.homeproducts

interface ProductsShowAllListener {
    fun showAllDiscountProducts(id: Long) = Unit
    fun showAllTopProducts(id: Long) = Unit
    fun showAllNoveltiesProducts(id: Long) = Unit
    fun showAllBottomProducts(id: Long) = Unit
}