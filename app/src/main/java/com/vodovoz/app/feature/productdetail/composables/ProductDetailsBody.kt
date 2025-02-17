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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.CommentUi
import com.vodovoz.app.design_system.model.ProductDetailsButtonsUi
import com.vodovoz.app.design_system.model.ProductDetailsUi
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.SectionUi
import com.vodovoz.app.util.calculateProductPrice
import com.vodovoz.app.util.formatPrice
import kotlin.math.roundToInt

@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsBody(
    modifier: Modifier = Modifier,
    productDetails: ProductDetailsUi,
    comments: List<CommentUi>,
    sectionAccessory: SectionUi<ProductUi>,
    sectionSimilarProducts: SectionUi<ProductUi>,
    buttons: ProductDetailsButtonsUi,

    productCartQuantity: Int,
    showDetailText: Boolean,
    showAllProperties: Boolean,
    quantityButtonIsLoading: Boolean,
    onFloatingButtonChange: (Boolean) -> Unit,
    onProductImageClick: (String) -> Unit,
    onDetailPreviewTextShowOrHide: () -> Unit,
    onAllPropertiesShow: () -> Unit,
    onAddToCart: () -> Unit,
    onProductMinus: () -> Unit,
    onProductPlus: () -> Unit,
    onNavigateToCart: () -> Unit,
    onAboutProductClick: () -> Unit,

    onShowAllCommentsClick: () -> Unit,

    onMultiButtonClick: () -> Unit,
    onPresentButtonClick: () -> Unit,
    onPreOrderButtonClick: () -> Unit,
    onAnalogButtonClick: () -> Unit,
    onPresentBlockButtonClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        ProductDetailsImagePager(
            productImages = productDetails.pictures,
            onImageClick = { image ->
                onProductImageClick(image)
            }
        )

        ProductDetailsLabels(
            modifier = Modifier.padding(top = 24.dp),
            labels = productDetails.labels
        )

        Text(
            text = productDetails.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp)
        )

        ProductDetailsRatingBar(
            modifier = Modifier.padding(top = 16.dp),
            rating = productDetails.rating.formatPrice(),
            numberOfReviews = productDetails.commentsCount,
            articleNumber = productDetails.barCode,
            onReviewsClick = {

            },
            onCopyClick = {

            },
        )

        ProductDetailsPriceInfo(
            modifier = Modifier.padding(top = 24.dp),
            deposit = productDetails.deposit,
            firstPrice = productDetails.firstPrice,
            pricePerUnit = productDetails.pricePerUnit ?: ""
        )


        ProductDetailsButtonsBlock(
            isAvailable = productDetails.isAvailable,
            quantityButtonIsLoading = quantityButtonIsLoading,
            cartQuantity = productCartQuantity,
            buttons = buttons,
            totalPrice = calculateProductPrice(
                productCartQuantity,
                productDetails.prices
            ).roundToInt(),
            onProductMinus = onProductMinus,
            onProductPlus = onProductPlus,
            onNavigateToCart = onNavigateToCart,
            onAddToCart = onAddToCart,
            onFloatingButtonChange = onFloatingButtonChange,
            onPresentButtonClick = onPresentButtonClick,
            onMultiButtonClick = onMultiButtonClick,
            onPreOrderButtonClick = onPreOrderButtonClick,
            onAnalogButtonClick = onAnalogButtonClick,
            onPresentBlockButtonClick = onPresentBlockButtonClick
        )

        ProductDetailsInfo(
            modifier = Modifier.padding(top = 32.dp),
            onAboutProductClick = onAboutProductClick,
            detailInfo = productDetails.detailInfo,
            showDetailText = showDetailText,
            onDetailTextSwitch = onDetailPreviewTextShowOrHide,
            showAllProperties = showAllProperties,
            onAllPropertiesShow = onAllPropertiesShow,
            contentBlockCharacteristics = productDetails.characteristics
        )

        val blockBrandCategory = productDetails.blockBrandCategory

        ProductDetailsCategoryAndBrand(
            modifier = Modifier.padding(top = 32.dp),
            category = blockBrandCategory.category,
            brand = blockBrandCategory.brand,
            onBrandClick = { brand ->

            },
            onCategoryClick = { category ->

            }
        )


        if (productDetails.sectionTags.items.isNotEmpty()) {
            ProductDetailsSearchWords(
                modifier = Modifier.padding(top = 32.dp),
                sectionTags = productDetails.sectionTags
            )
        }


        ProductDetailsComments(
            modifier = Modifier.padding(top = 32.dp),
            commentsCount = productDetails.commentsCount,
            comments = comments,
            onLeaveRateClick = {

            },
            onShowAllCommentsClick = onShowAllCommentsClick
        )


        if (sectionAccessory.items.isNotEmpty()) {
            ProductDetailsAccessoryProducts(
                modifier = Modifier.padding(top = 32.dp),
                sectionAccessory = sectionAccessory,
                onProductLike = {},
                onProductClick = {}
            )
        }


        if (sectionSimilarProducts.items.isNotEmpty()) {
            ProductDetailsSimilarProducts(
                modifier = Modifier.padding(top = 32.dp),
                sectionSimilarProducts = sectionSimilarProducts,
                onProductLike = {},
                onProductClick = {}
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

    }
}




















