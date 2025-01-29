package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.ProductQuantityWithCartButton
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.isElementVisible
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.calculateProductPrice
import kotlin.math.roundToInt

@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsBody(
    modifier: Modifier = Modifier,
    productDetailUi: ProductDetailUI,
    category: CategoryUI,
    comments: List<CommentUI>,
    viewedProductUIList: List<ProductUI>,
    deposit: Int,
    articleNumber: String,
    productCartQuantity: Int,
    buyWithProductUIList: List<ProductUI>,
    showDetailPreviewText: Boolean,
    showAllProperties: Boolean,
    searchWords: List<String>,
    quantityButtonIsLoading: Boolean,
    hideFloatingButton: Boolean,
    onFloatingButtonChange: (Boolean) -> Unit,
    onProductImageClick: (String) -> Unit,
    onDetailPreviewTextShowOrHide: () -> Unit,
    onAllPropertiesShow: () -> Unit,
    onAddToCart: () -> Unit,
    onProductMinus: () -> Unit,
    onProductPlus: () -> Unit,
    onNavigateToCart: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        ProductDetailsImagePager(
            productImages = productDetailUi.detailPictureList,
            onImageClick = { image ->
                onProductImageClick(image)
            }
        )

        ProductDetailLabelsRow(
            modifier = Modifier.padding(top = 24.dp),
            labelEntities = productDetailUi.labels
        )

        Text(
            text = productDetailUi.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        )

        //todo - put data
        ProductDetailsRatingBar(
            modifier = Modifier.padding(top = 16.dp),
            rating = productDetailUi.rating,
            numberOfReviews = productDetailUi.commentsAmount,
            articleNumber = articleNumber,
            onReviewsClick = {

            },
            onCopyClick = {

            },
        )

        ProductDetailsPriceInfo(
            modifier = Modifier.padding(top = 24.dp),
            priceUIList = productDetailUi.priceUIList,
            deposit = deposit
        )


        //todo - add to cart & upgrade button
        if (productCartQuantity > 0 || quantityButtonIsLoading) {
            ProductQuantityWithCartButton(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .isElementVisible(onFloatingButtonChange),
                onProductMinus = onProductMinus,
                onProductPlus = onProductPlus,
                onCartClick = onNavigateToCart,
                countProducts = productCartQuantity,
                currentPrice = calculateProductPrice(
                    productCartQuantity,
                    productDetailUi.priceUIList
                ).roundToInt(),
                isLoading = quantityButtonIsLoading
            )
        } else {
            VodovozButton(
                text = stringResource(R.string.to_cart),
                onClick = onAddToCart,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 16.dp)
                    .isElementVisible(onFloatingButtonChange)
            )
        }

        //todo - make onAboutProductClick
        ProductDetailsInfo(
            modifier = Modifier.padding(top = 32.dp),
            onAboutProductClick = { /*TODO*/ },
            previewText = productDetailUi.previewText,
            detailPreviewText = productDetailUi.detailText,
            showDetailPreviewText = showDetailPreviewText,
            onDetailPreviewTextShowOrHide = onDetailPreviewTextShowOrHide,
            properties = productDetailUi.propertiesGroupUIList.firstOrNull()?.propertyUIList
                ?: emptyList(),
            showAllProperties = showAllProperties,
            onAllPropertiesShow = onAllPropertiesShow
        )

        //todo - mb need fix
        ProductDetailsCategoryAndBrand(
            modifier = Modifier.padding(top = 32.dp),
            category = category,
            brand = productDetailUi.brandUI,
            onBrandClick = { brandUI ->

            },
            onCategoryClick = { categoryUI ->

            }
        )


        if (searchWords.isNotEmpty()) {
            ProductDetailsSearchWords(
                modifier = Modifier.padding(top = 32.dp),
                searchWords = searchWords
            )
        }


        //todo - put data
        ProductDetailsComments(
            modifier = Modifier.padding(top = 32.dp),
            commentsAmount = productDetailUi.commentsAmount,
            comments = comments,
            onLeaveRateClick = {

            }
        )


        //todo - clicks
        if (buyWithProductUIList.isNotEmpty()) {
            ProductDetailsBuyWithProduct(
                modifier = Modifier.padding(top = 32.dp),
                buyWithProductUIList = buyWithProductUIList,
                onProductLike = {},
                onProductClick = {}
            )
        }

        //todo - clicks
        if (viewedProductUIList.isNotEmpty()) {
            ProductDetailsViewedProducts(
                modifier = Modifier.padding(top = 32.dp),
                viewedProductsUIList = viewedProductUIList,
                onProductLike = {},
                onProductClick = {}
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

    }
}




















