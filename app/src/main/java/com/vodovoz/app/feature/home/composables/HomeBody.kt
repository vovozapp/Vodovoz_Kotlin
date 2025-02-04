package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.model.PromotionUi
import com.vodovoz.app.feature.home.model.CategoryWithProductsUi
import com.vodovoz.app.feature.home.model.MenuItemUi
import com.vodovoz.app.feature.home.model.OrderUi
import com.vodovoz.app.feature.home.model.OrderWithMenuUi
import com.vodovoz.app.feature.home.model.PopularCategoryUi
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.SectionUi
import com.vodovoz.app.ui.model.BannerUI

@Suppress("NonSkippableComposable")
@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    banners: List<BannerUI>,
    promotions: List<PromotionUi>,
    popularSections: List<PopularCategoryUi>,
    newProducts: List<ProductUi>,
    hurryUpBuyProducts: List<ProductUi>,
    orderWithMenu: OrderWithMenuUi,
    currentCategoryWithProducts: CategoryWithProductsUi,
    bestOffersSection: SectionUi<CategoryWithProductsUi>,
    bottomSection: SectionUi<CategoryWithProductsUi>,
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


        //todo - put actual data

        val pagerState = rememberPagerState {
            banners.count()
        }

        AuthScrollImagePager(
            modifier = Modifier.padding(top = 8.dp),
            images = banners.map { it.detailPicture },
            onImageClick = {

            },
            pagerState = pagerState,
            pageWidth = 300.dp
        )

        //todo - put stories
        if (false) {
            HomeStoriesRow(
                modifier = Modifier.padding(top = 16.dp),
                storiesImages = listOf(
                    "https://vodovoz.net/upload/iblock/d9c/3yue4g53w2d79bukaaz7u0u8ym7b95tr.jpg",
                    "https://vodovoz.net/upload/iblock/8f9/sazr2hm139ok02s3oj80q0tsqr5f3ibf.jpg",
                    "https://vodovoz.net/upload/iblock/1eb/xgy856ctgho2l3bn8fzjcmqeug0tvbiw.jpg",
                    "https://vodovoz.net/upload/iblock/1eb/xgy856ctgho2l3bn8fzjcmqeug0tvbiw.jpg",
                    "https://vodovoz.net/upload/iblock/1eb/xgy856ctgho2l3bn8fzjcmqeug0tvbiw.jpg",
                    "https://vodovoz.net/upload/iblock/1eb/xgy856ctgho2l3bn8fzjcmqeug0tvbiw.jpg"
                ),
                onStoryClick = {

                }
            )
        }


        if (orderWithMenu.order != null || orderWithMenu.menuItems.isNotEmpty()) {
            HomeMenuRow(
                modifier = Modifier.padding(top = 24.dp),
                orderWithMenu = orderWithMenu,
                onOrderClick = onOrderClick,
                onMenuItemClick = onMenuItemClick
            )
        }

        HomeDivider(
            modifier = Modifier.padding(top = 4.dp)
        )


        //todo - put actual data
        HomePopularSections(
            modifier = Modifier.padding(top = 4.dp),
            onShowAllClick = {

            },
            onPopularSectionClick = onPopularSectionClick,
            popularSections = popularSections
        )


        //todo - put actual data
        HomeBestOffers(
            modifier = Modifier.padding(top = 32.dp),
            onShowAllClick = {

            },
            currentCategoryWithProducts = currentCategoryWithProducts,
            categoryWithProductsList = bestOffersSection.items,
            onCategorySelect = onCategorySelect,
            onProductClick = {},
            onProductLike = {}
        )

        //todo - put actual data
        HomeHurryUpBuyProducts(
            modifier = Modifier.padding(top = 32.dp),
            hurryUpBuyProducts = hurryUpBuyProducts,
            onShowAllClick = { },
            onProductClick = { },
            onProductLike = { }
        )

        //todo = put actual data
        HomeNewProducts(
            modifier = Modifier.padding(top = 32.dp),
            newProducts = newProducts,
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
            promotions = promotions
        )


        //todo = put actual data
        HomeBottledWater(
            modifier = Modifier.padding(top = 32.dp),
            products = bottomSection.items.firstOrNull()?.products
                ?: emptyList(),
            onProductClick = {},
            onProductLike = {},
            onShowAllClick = {}
        )

        //todo = put actual data
        HomeViewedProducts(
            modifier = Modifier.padding(top = 32.dp),
            products = newProducts,
            onProductClick = {},
            onProductLike = {},
            onShowAllClick = {}
        )



        Spacer(modifier = Modifier.height(24.dp))
    }
}