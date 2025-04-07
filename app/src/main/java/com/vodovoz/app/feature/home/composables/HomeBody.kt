package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.model.AboutAdvertisingUi
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
    onAboutAdvertisingClick: (AboutAdvertisingUi) -> Unit,
    onBannerClick: (BannerUi) -> Unit,
    onIncrementProductToCart: (ProductUi) -> Unit,
    onDecrementProductToCart: (ProductUi) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (banners.isNotEmpty()) {
            HomeBanners(
                banners = banners,
                onAdvertisingClick = onAboutAdvertisingClick,
                onBannerClick = onBannerClick
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

        HomeDivider(modifier = Modifier.padding(top = 4.dp))

        HomePopularCategories(
            modifier = Modifier.padding(top = 4.dp),
            onPopularCategoryClick = onPopularCategoryClick,
            sectionPopularCategories = sectionPopularCategories
        )

        HomeTopProducts(
            modifier = Modifier.padding(top = 32.dp),
            lazyListState = topProductsLazyListState,
            onShowAllClick = onShowAllClick,
            currentCategoryWithProducts = currentCategoryWithProducts,
            sectionCategoriesWithProducts = sectionTop,
            onCategorySelect = onCategorySelect,
            onProductClick = onProductCardClick,
            onProductLike = onProductLike,
            onIncrementToCart = onIncrementProductToCart,
            onDecrementToCart = onDecrementProductToCart
        )

        HomeProductsRow(
            modifier = Modifier.padding(top = 32.dp),
            sectionProducts = sectionHurryUpBuyProducts,
            onProductClick = onProductCardClick,
            onShowAllClick = onShowAllClick,
            onProductLike = onProductLike,
            onDecrementToCart = onDecrementProductToCart,
            onIncrementToCart = onIncrementProductToCart
        )

        HomeProductsRow(
            modifier = Modifier.padding(top = 32.dp),
            sectionProducts = sectionNewProducts,
            onProductClick = onProductCardClick,
            onProductLike = onProductLike,
            onShowAllClick = onShowAllClick,
            onDecrementToCart = onDecrementProductToCart,
            onIncrementToCart = onIncrementProductToCart
        )

        HomePromotions(
            modifier = Modifier.padding(top = 32.dp),
            onShowAllClick = onShowAllClick,
            onPromotionClick = onPromotionClick,
            sectionPromotions = sectionPromotions,
            onAboutAdvertisingClick = onAboutAdvertisingClick
        )


        HomeBottomProducts(
            modifier = Modifier.padding(top = 32.dp),
            sectionBottomProducts = sectionBottomProducts,
            onProductClick = onProductCardClick,
            onProductLike = onProductLike,
            onShowAllClick = onShowAllClick,
            onDecrementToCart = onDecrementProductToCart,
            onIncrementToCart = onIncrementProductToCart
        )

        HomeProductsRow(
            modifier = Modifier.padding(top = 32.dp),
            sectionProducts = sectionViewedProducts,
            onShowAllClick = onShowAllClick,
            onProductClick = onProductCardClick,
            onProductLike = onProductLike,
            onDecrementToCart = onDecrementProductToCart,
            onIncrementToCart = onIncrementProductToCart
        )

        Spacer(modifier = Modifier.height(24.dp))

    }
}