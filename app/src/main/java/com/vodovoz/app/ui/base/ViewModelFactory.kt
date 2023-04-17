package com.vodovoz.app.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.feature.home.old.HomeViewModel
import com.vodovoz.app.feature.bottom.aboutapp.old.AboutAppDialogViewModel
import com.vodovoz.app.feature.bottom.services.old.AboutServicesViewModel
import com.vodovoz.app.feature.all.brands.old.AllBrandsViewModel
import com.vodovoz.app.feature.all.comments.old.AllCommentsByProductViewModel
import com.vodovoz.app.feature.all.promotions.old.AllPromotionsViewModel
import com.vodovoz.app.ui.fragment.bottles.AllBottlesViewModel
import com.vodovoz.app.ui.fragment.bottom_dialog_add_address.AddAddressViewModel
import com.vodovoz.app.feature.cart.old.CartViewModel
import com.vodovoz.app.feature.catalog.old.CatalogViewModel
import com.vodovoz.app.ui.fragment.concrete_filter.ConcreteFilterViewModel
import com.vodovoz.app.ui.fragment.contacts.ContactsViewModel
import com.vodovoz.app.ui.fragment.discount_card.DiscountCardViewModel
import com.vodovoz.app.feature.favorite.old.FavoriteViewModel
import com.vodovoz.app.ui.fragment.full_screen_history_slider.FullScreenHistoriesSliderViewModel
import com.vodovoz.app.feature.auth.login.old.LoginViewModel
import com.vodovoz.app.feature.map.old.MapViewModel
import com.vodovoz.app.feature.all.orders.detail.old.OrderDetailsViewModel
import com.vodovoz.app.ui.fragment.ordering.OrderingViewModel
import com.vodovoz.app.feature.all.orders.old.OrdersHistoryViewModel
import com.vodovoz.app.feature.productlist.old.PaginatedProductsCatalogViewModel
import com.vodovoz.app.feature.productlistnofilter.old.PaginatedProductsCatalogWithoutFiltersViewModel
import com.vodovoz.app.feature.pastpurchases.old.PastPurchasesViewModel
import com.vodovoz.app.feature.preorder.old.PreOrderViewModel
import com.vodovoz.app.feature.productdetail.old.ProductDetailsViewModel
import com.vodovoz.app.ui.fragment.product_filters.ProductFiltersViewModel
import com.vodovoz.app.feature.onlyproducts.old.ProductsCatalogViewModel
import com.vodovoz.app.feature.profile.old.ProfileViewModel
import com.vodovoz.app.feature.promotiondetail.old.PromotionDetailsViewModel
import com.vodovoz.app.ui.fragment.questionnaires.QuestionnairesViewModel
import com.vodovoz.app.feature.auth.reg.old.RegisterViewModel
import com.vodovoz.app.feature.replacement.old.ReplacementProductsSelectionViewModel
import com.vodovoz.app.ui.fragment.saved_addresses.SavedAddressesViewModel
import com.vodovoz.app.feature.search.old.SearchViewModel
import com.vodovoz.app.feature.productdetail.sendcomment.old.SendCommentAboutProductViewModel
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

        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)){
            return ProductDetailsViewModel(dataRepository) as T
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

        if (modelClass.isAssignableFrom(OrderingViewModel::class.java)){
            return OrderingViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(AllBottlesViewModel::class.java)){
            return AllBottlesViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(ReplacementProductsSelectionViewModel::class.java)){
            return ReplacementProductsSelectionViewModel(dataRepository) as T
        }

        if (modelClass.isAssignableFrom(PreOrderViewModel::class.java)){
            return PreOrderViewModel(dataRepository) as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }

}