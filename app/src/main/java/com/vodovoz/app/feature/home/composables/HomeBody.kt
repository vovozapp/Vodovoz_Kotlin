package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.model.BannerUi
import com.vodovoz.app.design_system.model.PromotionUi
import com.vodovoz.app.design_system.model.StoryUi
import com.vodovoz.app.feature.home.model.CategoryWithProductsUi
import com.vodovoz.app.feature.home.model.MenuItemUi
import com.vodovoz.app.feature.home.model.OrderUi
import com.vodovoz.app.feature.home.model.OrderWithMenuUi
import com.vodovoz.app.feature.home.model.PopularCategoryUi
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.SectionUi

@Suppress("NonSkippableComposable")
@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    banners: List<BannerUi>,
    stories: List<StoryUi>,
    sectionPromotions: SectionUi<PromotionUi>,
    sectionPopularCategories: SectionUi<PopularCategoryUi>,
    sectionNewProducts: SectionUi<ProductUi>,
    sectionHurryUpBuyProducts: SectionUi<ProductUi>,
    orderWithMenu: OrderWithMenuUi,
    currentCategoryWithProducts: CategoryWithProductsUi,
    bestOffersSection: SectionUi<CategoryWithProductsUi>,
    sectionBottomProducts: SectionUi<CategoryWithProductsUi>,
    onStoryClick: (StoryUi) -> Unit,
    onCategorySelect: (CategoryWithProductsUi) -> Unit,
    onPopularSectionClick: (PopularCategoryUi) -> Unit,
    onOrderClick: (OrderUi) -> Unit,
    onMenuItemClick: (MenuItemUi) -> Unit,
    onShowAllPromotionClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        if (banners.isNotEmpty()) {
            HomeBanners(
                banners = banners,
                onBannerClick = {

                }
            )
        }

        if (stories.isNotEmpty()) {
            HomeStories(
                modifier = Modifier.padding(top = 16.dp),
                stories = stories,
                onStoryClick = onStoryClick
            )
        }


        if (orderWithMenu.order != null || orderWithMenu.menuItems.isNotEmpty()) {
            HomeOrderMenu(
                modifier = Modifier.padding(top = 24.dp),
                orderWithMenu = orderWithMenu,
                onOrderClick = onOrderClick,
                onMenuItemClick = onMenuItemClick
            )
        }

        HomeDivider(
            modifier = Modifier.padding(top = 4.dp)
        )

        HomePopularCategories(
            modifier = Modifier.padding(top = 4.dp),
            onPopularCategoryClick = onPopularSectionClick,
            sectionPopularCategories = sectionPopularCategories
        )

        //todo - put actual data
        HomeBestOffers(
            modifier = Modifier.padding(top = 32.dp),
            onShowAllClick = {

            },
            currentCategoryWithProducts = currentCategoryWithProducts,
            sectionCategoriesWithProducts = bestOffersSection,
            onCategorySelect = onCategorySelect,
            onProductClick = {},
            onProductLike = {}
        )

        //todo - put actual data
        HomeHurryUpBuyProducts(
            modifier = Modifier.padding(top = 32.dp),
            sectionHurryUpBuyProducts = sectionHurryUpBuyProducts,
            onShowAllClick = { },
            onProductClick = { },
            onProductLike = { }
        )

        //todo = put actual data
        HomeNewProducts(
            modifier = Modifier.padding(top = 32.dp),
            sectionNewProducts = sectionNewProducts,
            onProductClick = {},
            onProductLike = {},
            onShowAllClick = {}
        )

        //todo = put actual data
        HomePromotions(
            modifier = Modifier.padding(top = 32.dp),
            onShowAllClick = onShowAllPromotionClick,
            onPromotionClick = {

            },
            sectionPromotions = sectionPromotions
        )


        //todo = put actual data
        HomeBottom(
            modifier = Modifier.padding(top = 32.dp),
            sectionBottomProducts = sectionBottomProducts,
            onProductClick = {},
            onProductLike = {},
            onShowAllClick = {}
        )

        //todo = put actual data
        HomeViewedProducts(
            modifier = Modifier.padding(top = 32.dp),
            sectionViewedProducts = sectionNewProducts,
            onProductClick = {},
            onProductLike = {},
            onShowAllClick = {}
        )



        Spacer(modifier = Modifier.height(24.dp))
    }
}