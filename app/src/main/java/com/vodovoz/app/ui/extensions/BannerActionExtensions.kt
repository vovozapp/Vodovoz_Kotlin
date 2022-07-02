package com.vodovoz.app.ui.extensions

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.ui.components.fragment.all_promotions.AllPromotionsFragment
import com.vodovoz.app.ui.components.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.components.fragment.products_catalog.ProductsCatalogFragment
import com.vodovoz.app.util.LogSettings

object BannerActionExtensions {

    fun ActionEntity.invoke(navController: NavController, activity: FragmentActivity)  {
        Log.i(LogSettings.LIFECYCLE_LOG, "${this::class.simpleName}")
        val navDirect = when(this) {
            is ActionEntity.Brand ->
                HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId = this.brandId)
                )
            is ActionEntity.Brands -> {
                HomeFragmentDirections.actionToAllBrandsFragment(this.brandIdList.toLongArray())
            }
            is ActionEntity.Product ->
                HomeFragmentDirections.actionToProductDetailFragment(this.productId)
            is ActionEntity.Products ->
                HomeFragmentDirections.actionToProductsCatalogFragment(
                    ProductsCatalogFragment.DataSource.BannerProducts(categoryId = this.categoryId)
                )
            is ActionEntity.Promotion ->
                HomeFragmentDirections.actionToPromotionDetailFragment(this.promotionId)
            is ActionEntity.Promotions -> HomeFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.ByBanner(this.categoryId)
            )
            is ActionEntity.AllPromotions -> HomeFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.All()
            )
            is ActionEntity.Link -> {
                val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this.url))
                activity.startActivity(openLinkIntent)
                null
            }
            is ActionEntity.Category ->
                HomeFragmentDirections.actionToPaginatedProductsCatalogFragment(this.categoryId)
            is ActionEntity.Discount -> HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
            )
            is ActionEntity.Novelties -> HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
            )
        }
        navDirect?.let { navController.navigate(navDirect) }
    }

}