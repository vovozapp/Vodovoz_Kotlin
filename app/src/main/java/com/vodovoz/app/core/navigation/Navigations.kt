package com.vodovoz.app.core.navigation

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.vodovoz.app.R
import com.vodovoz.app.design_system.model.ParentCategoryUi
import com.vodovoz.app.design_system.model.filters.FilterUi
import com.vodovoz.app.design_system.model.filters.FiltersUi
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.cart.model.CartPresentPopupWindowUi
import com.vodovoz.app.feature.cart.model.CartPresentUi
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment


private val ProfileNavOptions = NavOptions.Builder()
    .setEnterAnim(R.anim.slide_in_right)
    .setExitAnim(R.anim.fade_out)
    .setPopExitAnim(R.anim.slide_out_right)
    .build()

fun NavController.navigateToOrderQuestion(orderId: Long) {
    navigate(
        R.id.orderQuestionFragment,
        bundleOf("orderId" to orderId),
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_botton)
            .setExitAnim(R.anim.fade_out)
            .setPopExitAnim(R.anim.slide_out_botton)
            .setPopEnterAnim(R.anim.fade_in)
            .build()
    )
}

fun NavController.navigateToAllBottles() {
    navigate(
        R.id.allBottlesFragment,
        Bundle.EMPTY,
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.fade_out)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    )
}

fun NavController.navigateToGifts(
    present: CartPresentUi? = null,
    popupWindow: CartPresentPopupWindowUi,
) {
    navigate(
        R.id.giftsFragment,
        bundleOf("present" to present, "popupWindow" to popupWindow),
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.fade_out)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    )
}

fun NavController.navigateToAddresses() {
    navigate(R.id.savedAddressesDialogFragment, Bundle.EMPTY, ProfileNavOptions)
}

fun NavController.navigateToRecoverPassword() {
    navigate(R.id.recoverPasswordFragment, Bundle.EMPTY, ProfileNavOptions)
}

fun NavController.navigateToButtonProductList(buttonId: Int) {
    navigate(
        R.id.paginatedProductsCatalogWithoutFiltersFragment,
        bundleOf(
            "dataSource" to PaginatedProductsCatalogWithoutFiltersFragment.DataSource.ButtonProducts(
                buttonId
            )
        )
    )
}

fun NavController.navigateToAboutApp() {
    navigate(R.id.aboutAppDialogFragment, Bundle.EMPTY, ProfileNavOptions)
}


fun NavController.navigateToNotificationSettings() {
    navigate(R.id.notificationSettingsFragment, Bundle.EMPTY, ProfileNavOptions)
}


fun NavController.navigateToQuestionnaires() {
    navigate(R.id.questionnairesFragment2, Bundle.EMPTY, ProfileNavOptions)
}

fun NavController.navigateToPastPurchases() {
    navigate(
        R.id.pastPurchasesFragment, Bundle.EMPTY, ProfileNavOptions
    )
}

fun NavController.navigateToOrdersHistory() {
    navigate(R.id.allOrdersFragment, Bundle.EMPTY, ProfileNavOptions)
}

fun NavController.navigateToOrderDetails(orderId: Int) {
    navigate(R.id.orderDetailsFragment, bundleOf("orderId" to orderId.toLong()))
}

fun NavController.navigateToProductImages(image: String, images: List<String>) {
    val array = images.toTypedArray()
    val currentImageIndex = images.indexOf(image)
    val bundle = bundleOf("startPosition" to currentImageIndex, "detailPictureList" to array)
    navigate(
        R.id.fullScreenDetailPicturesSliderFragment, bundle, NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()
    )
}

fun NavController.navigateToRutubeVideo(videoCode: String) {
    val bundle = bundleOf("videoId" to videoCode)
    navigate(
        R.id.ruTubeVideoFragmentDialog, bundle, NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()
    )
}


fun NavController.navigateToStories(storyId: Long) {
    val bundle = bundleOf("startHistoryId" to storyId)
    navigate(
        R.id.fullScreenHistorySliderFragment, bundle, NavOptions.Builder()
            .setEnterAnim(R.anim.scale_in)
            .setExitAnim(R.anim.fade_out)
            .setPopExitAnim(R.anim.slide_out_botton)
            .build()
    )
}

fun NavController.navigateToRegister() {
    navigate(R.id.registerFragment)
}

fun NavController.navigateToChangePassword() {
    navigate(
        R.id.changePasswordFragment, bundleOf(), NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.fade_out)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    )
}


fun NavController.navigateToUserData() {
    navigate(R.id.userDataFragment)
}

fun NavController.navigateToLoginByEmail() {
    navigate(R.id.loginByEmailFragment)
}


fun NavController.navigateToLogin() {
    navigate(R.id.loginFragment)
}

fun NavController.navigateToProductFilterValues(categoryId: Long, filter: FilterUi) {
    navigate(
        R.id.concreteFilterFragment,
        bundleOf("categoryId" to categoryId, "filter" to filter),
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    )
}

fun NavController.navigateToProductFilters(categoryId: Long, filters: FiltersUi) {
    navigate(
        R.id.productFiltersFragment,
        bundleOf(
            "categoryId" to categoryId,
            "filters" to filters
        ),
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_botton)
            .setExitAnim(R.anim.fade_out)
            .setPopExitAnim(R.anim.slide_out_botton)
            .build()
    )
}

fun NavController.navigateToProductComments(productId: Long) {
    navigate(
        R.id.productCommentsFragment,
        bundleOf("productId" to productId)
    )

}

fun NavController.navigateToPreOrder(productId: Long) {
    navigate(
        R.id.preOrderFragment,
        bundleOf("productId" to productId),
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_botton)
            .setExitAnim(R.anim.slide_out_botton)
            .setPopEnterAnim(R.anim.slide_in_botton)
            .setPopExitAnim(R.anim.slide_out_botton)
            .build()
    )

}

fun NavController.navigateToAnalogs(productId: Long) {
    navigate(
        R.id.productsCollectionFragment,
        bundleOf("productId" to productId)
    )
}

fun NavController.navigateToCertificateActivation() {
    navigate(
        R.id.certificateActivationFragment, bundleOf(), NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.fade_out)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    )
}

fun NavController.navigateToCategories(category: CategoryUi, categories: List<CategoryUi>) {
    navigate(
        R.id.categoriesFragment,
        bundleOf(
            "categoryList" to categories.toTypedArray(),
            "category" to category,
        )
    )
}


fun NavController.navigateToSubCategories(category: ParentCategoryUi) {
    navigate(
        R.id.subCategoriesFragment,
        bundleOf("category" to category),
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    )
}

fun NavController.navigateToBrandProductList(brandId: Long) {
    navigate(
        R.id.paginatedProductsCatalogWithoutFiltersFragment,
        bundleOf(
            "dataSource" to PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(
                brandId
            )
        )
    )
}

fun NavController.navigateToSearchProductList(query: String) {
    navigate(
        R.id.paginatedProductsCatalogWithoutFiltersFragment,
        bundleOf(
            "dataSource" to PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Search(
                query
            )
        )
    )
}


fun NavController.navigateToCategoryProductList(categoryId: Long) {
    navigate(
        R.id.paginatedProductsCatalogWithoutFiltersFragment,
        bundleOf(
            "dataSource" to PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Category(
                categoryId
            )
        )
    )
}

fun NavController.navigateToBannerProductList(bannerId: Long, blockId: Long) {
    navigate(
        R.id.paginatedProductsCatalogWithoutFiltersFragment,
        bundleOf(
            "dataSource" to PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Products(
                bannerId, blockId
            )
        )
    )

}

fun NavController.navigateToProductDetails(productId: Long) {
    navigate(
        R.id.productDetailFragment,
        bundleOf("productId" to productId)
    )
}

fun NavController.navigateToPromotionDetails(promotionId: Long) {
    navigate(
        R.id.promotionDetailFragment,
        bundleOf("promotionId" to promotionId)
    )
}

fun NavController.navigateToPromotions(blockId: Long, bannerId: Long) {
    navigate(
        R.id.allPromotionsFragment,
        bundleOf(
            "dataSource" to AllPromotionsFragment.DataSource.ByBanner(bannerId, blockId)
        )
    )
}

fun NavController.navigateToSearch(query: String = "") {
    navigate(R.id.searchFragment, bundleOf("query" to query))
}

fun NavController.navigateToHurryBuyUpProducts() {
    navigate(
        R.id.paginatedProductsCatalogWithoutFiltersFragment,
        bundleOf("dataSource" to PaginatedProductsCatalogWithoutFiltersFragment.DataSource.HurryBuyUpProducts)
    )
}

fun NavController.navigateToNewProducts() {
    navigate(
        R.id.paginatedProductsCatalogWithoutFiltersFragment,
        bundleOf("dataSource" to PaginatedProductsCatalogWithoutFiltersFragment.DataSource.NewProducts)
    )
}

fun NavController.navigateToPromotions() {
    navigate(R.id.allPromotionsFragment)
}

fun NavController.navigateToWaterApp() {
    navigate(R.id.waterAppFragment)
}

fun NavController.navigateToBuyCertificate() {
    navigate(R.id.buyCertificateFragment)
}

fun NavController.navigateToWebView(url: String, title: String) {
    navigate(R.id.webViewFragment, bundleOf("url" to url, "title" to title))

}