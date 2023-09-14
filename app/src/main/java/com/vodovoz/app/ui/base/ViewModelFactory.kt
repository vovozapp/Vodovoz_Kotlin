package com.vodovoz.app.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.feature.filters.concrete.ConcreteFilterViewModel
import com.vodovoz.app.feature.filters.product.ProductFiltersViewModel
import com.vodovoz.app.ui.fragment.some_products_by_brand.SomeProductsByBrandViewModel
import com.vodovoz.app.ui.fragment.some_products_maybe_like.SomeProductsMaybeLikeViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val dataRepository: DataRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {


        if (modelClass.isAssignableFrom(ProductFiltersViewModel::class.java)) {
            return ProductFiltersViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ConcreteFilterViewModel::class.java)) {
            return ConcreteFilterViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SomeProductsByBrandViewModel::class.java)) {
            return SomeProductsByBrandViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SomeProductsMaybeLikeViewModel::class.java)) {
            return SomeProductsMaybeLikeViewModel(dataRepository) as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }

}