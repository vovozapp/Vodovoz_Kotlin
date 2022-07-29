package com.vodovoz.app.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.fragment.about_app.AboutAppDialogViewModel
import com.vodovoz.app.ui.fragment.about_services.AboutServicesViewModel
import com.vodovoz.app.ui.fragment.all_brands.AllBrandsViewModel
import com.vodovoz.app.ui.fragment.all_comments_by_product.AllCommentsByProductViewModel
import com.vodovoz.app.ui.fragment.orders_history.OrdersHistoryViewModel
import com.vodovoz.app.ui.fragment.all_promotions.AllPromotionsViewModel
import com.vodovoz.app.ui.fragment.bottom_dialog_add_address.AddAddressViewModel
import com.vodovoz.app.ui.fragment.cart.CartViewModel
import com.vodovoz.app.ui.fragment.catalog.CatalogViewModel
import com.vodovoz.app.ui.fragment.concrete_filter.ConcreteFilterViewModel
import com.vodovoz.app.ui.fragment.contacts.ContactsViewModel
import com.vodovoz.app.ui.fragment.discount_card.DiscountCardViewModel
import com.vodovoz.app.ui.fragment.favorite.FavoriteViewModel
import com.vodovoz.app.ui.fragment.full_screen_history_slider.FullScreenHistoriesSliderViewModel
import com.vodovoz.app.ui.fragment.home.HomeViewModel
import com.vodovoz.app.ui.fragment.login.LoginViewModel
import com.vodovoz.app.ui.fragment.map.MapViewModel
import com.vodovoz.app.ui.fragment.order_details.OrderDetailsViewModel
import com.vodovoz.app.ui.fragment.paginated_products_catalog.PaginatedProductsCatalogViewModel
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersViewModel
import com.vodovoz.app.ui.fragment.past_purchases.PastPurchasesViewModel
import com.vodovoz.app.ui.fragment.product_detail.ProductDetailViewModel
import com.vodovoz.app.ui.fragment.product_filters.ProductFiltersViewModel
import com.vodovoz.app.ui.fragment.products_catalog.ProductsCatalogViewModel
import com.vodovoz.app.ui.fragment.profile.ProfileViewModel
import com.vodovoz.app.ui.fragment.promotion_details.PromotionDetailsViewModel
import com.vodovoz.app.ui.fragment.questionnaires.QuestionnairesViewModel
import com.vodovoz.app.ui.fragment.register.RegisterViewModel
import com.vodovoz.app.ui.fragment.saved_addresses.SavedAddressesViewModel
import com.vodovoz.app.ui.fragment.search.SearchViewModel
import com.vodovoz.app.ui.fragment.send_comment_about_product.SendCommentAboutProductViewModel
import com.vodovoz.app.ui.fragment.send_comment_about_shop.SendCommentAboutShopViewModel
import com.vodovoz.app.ui.fragment.service_detail.ServiceDetailViewModel
import com.vodovoz.app.ui.fragment.service_order.ServiceOrderViewModel
import com.vodovoz.app.ui.fragment.single_root_catalog.SingleRootCatalogViewModel
import com.vodovoz.app.ui.fragment.some_products_by_brand.SomeProductsByBrandViewModel
import com.vodovoz.app.ui.fragment.some_products_maybe_like.SomeProductsMaybeLikeViewModel
import com.vodovoz.app.ui.fragment.user_data.UserDataViewModel

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

        if (modelClass.isAssignableFrom(PromotionDetailsViewModel::class.java)){
            return PromotionDetailsViewModel(dataRepository) as T
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

        if (modelClass.isAssignableFrom(SavedAddressesViewModel::class.java)){
            return SavedAddressesViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(OrdersHistoryViewModel::class.java)){
            return OrdersHistoryViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(OrderDetailsViewModel::class.java)){
            return OrderDetailsViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SendCommentAboutShopViewModel::class.java)){
            return SendCommentAboutShopViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(SearchViewModel::class.java)){
            return SearchViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(PastPurchasesViewModel::class.java)){
            return PastPurchasesViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(DiscountCardViewModel::class.java)){
            return DiscountCardViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(QuestionnairesViewModel::class.java)){
            return QuestionnairesViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)){
            return ContactsViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ServiceDetailViewModel::class.java)){
            return ServiceDetailViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ServiceOrderViewModel::class.java)){
            return ServiceOrderViewModel(dataRepository) as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }

}