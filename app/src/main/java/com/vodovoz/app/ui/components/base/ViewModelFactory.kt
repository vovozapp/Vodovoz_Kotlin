package com.vodovoz.app.ui.components.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.fragment.login.LoginViewModel
import com.vodovoz.app.ui.components.fragment.account.AccountViewModel
import com.vodovoz.app.ui.components.fragment.catalog.CatalogViewModel
import com.vodovoz.app.ui.components.fragment.home.HomeViewModel
import com.vodovoz.app.ui.components.fragment.paginatedBrandProductList.PaginatedBrandProductListViewModel
import com.vodovoz.app.ui.components.fragment.concreteFilter.ConcreteFilterViewModel
import com.vodovoz.app.ui.components.fragment.allProductFilters.FiltersViewModel
import com.vodovoz.app.ui.components.adapter.homeBottomInfo.AdditionalInfoSectionViewModel
import com.vodovoz.app.ui.components.fragment.allPromotions.AllPromotionsViewModel
import com.vodovoz.app.ui.components.fragment.miniCatalog.MiniCatalogViewModel
import com.vodovoz.app.ui.components.fragment.paginatedMaybeLikeProductList.PaginatedMaybeLikeProductListViewModel
import com.vodovoz.app.ui.components.fragment.productDetail.ProductDetailViewModel
import com.vodovoz.app.ui.components.fragment.products.ProductsViewModel
import com.vodovoz.app.ui.components.fragment.register.RegisterViewModel
import com.vodovoz.app.ui.components.fragment.bannerSlider.BannerSliderViewModel
import com.vodovoz.app.ui.components.fragment.brandSlider.BrandSliderViewModel
import com.vodovoz.app.ui.components.fragment.commentSlider.CommentSliderViewModel
import com.vodovoz.app.ui.components.fragment.countrySlider.CountrySliderViewModel
import com.vodovoz.app.ui.components.fragment.historySlider.HistorySliderViewModel
import com.vodovoz.app.ui.components.fragment.orderSlider.OrderSliderViewModel
import com.vodovoz.app.ui.components.fragment.popularSlider.PopularSliderViewModel
import com.vodovoz.app.ui.components.fragment.productSlider.ProductSliderViewModel
import com.vodovoz.app.ui.components.fragment.promotionDetail.PromotionDetailViewModel
import com.vodovoz.app.ui.components.fragment.promotionSlider.PromotionSliderViewModel
import com.vodovoz.app.ui.components.fragment.userData.UserDataViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val dataRepository: DataRepository
) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BannerSliderViewModel::class.java)){
            return BannerSliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(HistorySliderViewModel::class.java)){
            return HistorySliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(PopularSliderViewModel::class.java)){
            return PopularSliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(AdditionalInfoSectionViewModel::class.java)){
            return AdditionalInfoSectionViewModel() as T
        }

        if (modelClass.isAssignableFrom(BrandSliderViewModel::class.java)){
            return BrandSliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(CountrySliderViewModel::class.java)){
            return CountrySliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ProductSliderViewModel::class.java)){
            return ProductSliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)){
            return CatalogViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)){
            return ProductsViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(CommentSliderViewModel::class.java)){
            return CommentSliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(PromotionSliderViewModel::class.java)){
            return PromotionSliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(FiltersViewModel::class.java)){
            return FiltersViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ConcreteFilterViewModel::class.java)){
            return ConcreteFilterViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(MiniCatalogViewModel::class.java)){
            return MiniCatalogViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)){
            return ProductDetailViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(PaginatedBrandProductListViewModel::class.java)){
            return PaginatedBrandProductListViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(PaginatedMaybeLikeProductListViewModel::class.java)){
            return PaginatedMaybeLikeProductListViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(AccountViewModel::class.java)){
            return AccountViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(UserDataViewModel::class.java)){
            return UserDataViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(OrderSliderViewModel::class.java)){
            return OrderSliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(PromotionDetailViewModel::class.java)){
            return PromotionDetailViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(AllPromotionsViewModel::class.java)){
            return AllPromotionsViewModel(dataRepository) as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }

}