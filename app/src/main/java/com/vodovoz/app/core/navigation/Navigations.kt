package com.vodovoz.app.core.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.catalog.CatalogFragmentDirections
import com.vodovoz.app.feature.catalog.model.CatalogCategoryUi
import com.vodovoz.app.feature.home.HomeFragmentDirections
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment


fun NavController.navigateToProductComments(productId: Long)    {
    navigate(
        R.id.productCommentsFragment,
        bundleOf("productId" to productId)
    )

}

fun NavController.navigateToPreOrder(productId: Long){
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

fun NavController.navigateToAnalogs(productId: Long){
    navigate(
        R.id.productsCollectionFragment,
        bundleOf("productId" to productId)
    )
}

fun NavController.navigateToCertificateActivation(){
    navigate(R.id.certificateActivationFragment)
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


fun NavController.navigateToSubCategories(category: CatalogCategoryUi) {
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
        bundleOf("dataSource" to AllPromotionsFragment.DataSource.ByBanner(blockId))
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

fun NavController.navigateToPromotions(){
    navigate(R.id.allPromotionsFragment)
}

fun NavController.navigateToWaterApp(){
    navigate(R.id.waterAppFragment)
}

fun NavController.navigateToBuyCertificate(){
    navigate(R.id.buyCertificateFragment)
}

fun NavController.navigateToWebView(url: String, title: String){
    navigate(R.id.webViewFragment, bundleOf("url" to url, "title" to title))

}