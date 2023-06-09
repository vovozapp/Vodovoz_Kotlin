package com.vodovoz.app.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.feature.all.orders.detail.old.OrderDetailsViewModel
import com.vodovoz.app.feature.filters.concrete.ConcreteFilterViewModel
import com.vodovoz.app.feature.filters.product.ProductFiltersViewModel
import com.vodovoz.app.ui.fragment.full_screen_history_slider.FullScreenHistoriesSliderViewModel
import com.vodovoz.app.ui.fragment.questionnaires.QuestionnairesViewModel
import com.vodovoz.app.ui.fragment.send_comment_about_shop.SendCommentAboutShopViewModel
import com.vodovoz.app.ui.fragment.service_order.ServiceOrderViewModel
import com.vodovoz.app.ui.fragment.some_products_by_brand.SomeProductsByBrandViewModel
import com.vodovoz.app.ui.fragment.some_products_maybe_like.SomeProductsMaybeLikeViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val dataRepository: DataRepository
) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {


        if (modelClass.isAssignableFrom(ProductFiltersViewModel::class.java)){
            return ProductFiltersViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ConcreteFilterViewModel::class.java)){
            return ConcreteFilterViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SomeProductsByBrandViewModel::class.java)){
            return SomeProductsByBrandViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SomeProductsMaybeLikeViewModel::class.java)){
            return SomeProductsMaybeLikeViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(FullScreenHistoriesSliderViewModel::class.java)){
            return FullScreenHistoriesSliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(OrderDetailsViewModel::class.java)){
            return OrderDetailsViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SendCommentAboutShopViewModel::class.java)){
            return SendCommentAboutShopViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(QuestionnairesViewModel::class.java)){
            return QuestionnairesViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ServiceOrderViewModel::class.java)){
            return ServiceOrderViewModel(dataRepository) as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }

}