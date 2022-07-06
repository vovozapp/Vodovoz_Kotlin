package com.vodovoz.app.ui.components.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.fragment.about_app.AboutAppDialogViewModel
import com.vodovoz.app.ui.components.fragment.about_services.AboutServicesViewModel
import com.vodovoz.app.ui.components.fragment.all_brands.AllBrandsViewModel
import com.vodovoz.app.ui.components.fragment.all_comments_by_product.AllCommentsByProductViewModel
import com.vodovoz.app.ui.components.fragment.all_promotions.AllPromotionsViewModel
import com.vodovoz.app.ui.components.fragment.bottom_dialog_add_address.AddAddressViewModel
import com.vodovoz.app.ui.components.fragment.cart.CartViewModel
import com.vodovoz.app.ui.components.fragment.catalog.CatalogViewModel
import com.vodovoz.app.ui.components.fragment.concrete_filter.ConcreteFilterViewModel
import com.vodovoz.app.ui.components.fragment.favorite.FavoriteViewModel
import com.vodovoz.app.ui.components.fragment.full_screen_history_slider.FullScreenHistoriesSliderViewModel
import com.vodovoz.app.ui.components.fragment.home.HomeViewModel
import com.vodovoz.app.ui.components.fragment.login.LoginViewModel
import com.vodovoz.app.ui.components.fragment.map.MapViewModel
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog.PaginatedProductsCatalogViewModel
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersViewModel
import com.vodovoz.app.ui.components.fragment.product_detail.ProductDetailViewModel
import com.vodovoz.app.ui.components.fragment.product_filters.ProductFiltersViewModel
import com.vodovoz.app.ui.components.fragment.products_catalog.ProductsCatalogViewModel
import com.vodovoz.app.ui.components.fragment.profile.ProfileViewModel
import com.vodovoz.app.ui.components.fragment.promotion_detail.PromotionDetailViewModel
import com.vodovoz.app.ui.components.fragment.register.RegisterViewModel
import com.vodovoz.app.ui.components.fragment.send_comment_about_product.SendCommentAboutProductViewModel
import com.vodovoz.app.ui.components.fragment.single_root_catalog.SingleRootCatalogViewModel
import com.vodovoz.app.ui.components.fragment.some_products_by_brand.SomeProductsByBrandViewModel
import com.vodovoz.app.ui.components.fragment.some_products_maybe_like.SomeProductsMaybeLikeViewModel
import com.vodovoz.app.ui.components.fragment.user_data.UserDataViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val dataRepository: DataRepository
) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)){
            return CatalogViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(PaginatedProductsCatalogViewModel::class.java)){
            return PaginatedProductsCatalogViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ProductFiltersViewModel::class.java)){
            return ProductFiltersViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ConcreteFilterViewModel::class.java)){
            return ConcreteFilterViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SingleRootCatalogViewModel::class.java)){
            return SingleRootCatalogViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)){
            return ProductDetailViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SomeProductsByBrandViewModel::class.java)){
            return SomeProductsByBrandViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SomeProductsMaybeLikeViewModel::class.java)){
            return SomeProductsMaybeLikeViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)){
            return ProfileViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(UserDataViewModel::class.java)){
            return UserDataViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(PromotionDetailViewModel::class.java)){
            return PromotionDetailViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(AllPromotionsViewModel::class.java)){
            return AllPromotionsViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(PaginatedProductsCatalogWithoutFiltersViewModel::class.java)){
            return PaginatedProductsCatalogWithoutFiltersViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(AllBrandsViewModel::class.java)){
            return AllBrandsViewModel(dataRepository) as T
        }


        if (modelClass.isAssignableFrom(ProductsCatalogViewModel::class.java)){
            return ProductsCatalogViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(CartViewModel::class.java)){
            return CartViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(FullScreenHistoriesSliderViewModel::class.java)){
            return FullScreenHistoriesSliderViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(AllCommentsByProductViewModel::class.java)){
            return AllCommentsByProductViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SendCommentAboutProductViewModel::class.java)){
            return SendCommentAboutProductViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(AboutServicesViewModel::class.java)){
            return AboutServicesViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(AboutAppDialogViewModel::class.java)){
            return AboutAppDialogViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            return FavoriteViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(MapViewModel::class.java)){
            return MapViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(AddAddressViewModel::class.java)){
            return AddAddressViewModel(dataRepository) as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }

}