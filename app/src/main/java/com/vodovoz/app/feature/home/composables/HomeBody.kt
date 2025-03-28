package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.model.BannerUi
import com.vodovoz.app.design_system.model.CategoryWithProductsUi
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.PromotionUi
import com.vodovoz.app.design_system.model.SectionUi
import com.vodovoz.app.design_system.model.StoryUi
import com.vodovoz.app.domain.general.model.ButtonAction
import com.vodovoz.app.feature.home.model.MenuItemUi
import com.vodovoz.app.feature.home.model.OrderUi
import com.vodovoz.app.feature.home.model.OrderWithMenuUi
import com.vodovoz.app.feature.home.model.PopularCategoryUi

@Suppress("NonSkippableComposable")
@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    topProductsLazyListState: LazyListState,
    banners: List<BannerUi>,
    stories: List<StoryUi>,
    sectionPromotions: SectionUi<PromotionUi>,
    sectionPopularCategories: SectionUi<PopularCategoryUi>,
    sectionNewProducts: SectionUi<ProductUi>,
    sectionViewedProducts: SectionUi<ProductUi>,
    sectionHurryUpBuyProducts: SectionUi<ProductUi>,
    orderWithMenu: OrderWithMenuUi,
    currentCategoryWithProducts: CategoryWithProductsUi,
    sectionTop: SectionUi<CategoryWithProductsUi>,
    sectionBottomProducts: SectionUi<CategoryWithProductsUi>,
    onStoryClick: (StoryUi) -> Unit,
    onCategorySelect: (CategoryWithProductsUi) -> Unit,
    onPopularCategoryClick: (PopularCategoryUi) -> Unit,
    onOrderClick: (OrderUi) -> Unit,
    onMenuItemClick: (MenuItemUi) -> Unit,
    onShowAllClick: (ButtonAction) -> Unit,
    onProductCardClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onPromotionClick: (PromotionUi) -> Unit,
) {

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            if (banners.isNotEmpty()) {
                HomeBanners(
                    banners = banners,
                    onBannerClick = {

                    }
                )
            }
        }

        item {
            if (stories.isNotEmpty()) {
                HomeStories(
                    modifier = Modifier.padding(top = 16.dp),
                    stories = stories,
                    onStoryClick = onStoryClick
                )
            }
        }

        item {
            if (orderWithMenu.order != null || orderWithMenu.menuItems.isNotEmpty()) {
                HomeOrderMenu(
                    modifier = Modifier.padding(top = 24.dp),
                    orderWithMenu = orderWithMenu,
                    onOrderClick = onOrderClick,
                    onMenuItemClick = onMenuItemClick
                )
            }
        }


        item {
            HomeDivider(
                modifier = Modifier.padding(top = 4.dp)
            )

            HomePopularCategories(
                modifier = Modifier.padding(top = 4.dp),
                onPopularCategoryClick = onPopularCategoryClick,
                sectionPopularCategories = sectionPopularCategories
            )
        }

        item {
            HomeTopProducts(
                modifier = Modifier.padding(top = 32.dp),
                lazyListState = topProductsLazyListState,
                onShowAllClick = onShowAllClick,
                currentCategoryWithProducts = currentCategoryWithProducts,
                sectionCategoriesWithProducts = sectionTop,
                onCategorySelect = onCategorySelect,
                onProductClick = onProductCardClick,
                onProductLike = onProductLike
            )

        }

        item {
            HomeHurryUpBuyProducts(
                modifier = Modifier.padding(top = 32.dp),
                sectionHurryUpBuyProducts = sectionHurryUpBuyProducts,
                onProductClick = onProductCardClick,
                onShowAllClick = onShowAllClick,
                onProductLike = onProductLike
            )
        }

        item {
            HomeNewProducts(
                modifier = Modifier.padding(top = 32.dp),
                sectionNewProducts = sectionNewProducts,
                onProductClick = onProductCardClick,
                onProductLike = onProductLike,
                onShowAllClick = onShowAllClick
            )

        }

        item {
            HomePromotions(
                modifier = Modifier.padding(top = 32.dp),
                onShowAllClick = onShowAllClick,
                onPromotionClick = onPromotionClick,
                sectionPromotions = sectionPromotions
            )

        }

        item {
            HomeBottomProducts(
                modifier = Modifier.padding(top = 32.dp),
                sectionBottomProducts = sectionBottomProducts,
                onProductClick = onProductCardClick,
                onProductLike = onProductLike,
                onShowAllClick = onShowAllClick
            )

        }


        item {
            if (sectionViewedProducts.items.isNotEmpty()) {
                HomeViewedProducts(
                    modifier = Modifier.padding(top = 32.dp),
                    sectionViewedProducts = sectionViewedProducts,
                    onProductClick = onProductCardClick,
                    onProductLike = onProductLike,
                    onShowAllClick = onShowAllClick
                )
            }

        }


        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}