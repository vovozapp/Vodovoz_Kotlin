package com.vodovoz.app.feature.promotiondetail.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.paging.CombinedLoadStates
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.design_system.composables.chip.TimeLeftChip
import com.vodovoz.app.design_system.composables.chip.VodovozColorChip
import com.vodovoz.app.design_system.composables.list.gridProducts
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.PromotionDetailsUi
import com.vodovoz.app.design_system.vodovozTextLinkStyle

@Suppress("NonSkippableComposable")
@Composable
fun PromotionDetailsBody(
    modifier: Modifier = Modifier,
    promotionDetails: PromotionDetailsUi,
    products: List<ProductUi>,
    productsLoadStates: CombinedLoadStates,
    productsTitle: String,
    onHyperlinkClick: (String) -> Unit,
    onProductAnalogsClick: (ProductUi) -> Unit,
    onIncrementProductToCart: (ProductUi) -> Unit,
    onDecrementProductToCart: (ProductUi) -> Unit,
    onProductClick: (ProductUi) -> Unit,
    onProductLike: (ProductUi) -> Unit,
    onProductSee: (Int) -> Unit,
) {

    val lazyGridState = rememberLazyGridState()
    val shimmerState = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        state = lazyGridState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(promotionDetails.picture).crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val label = promotionDetails.label

                    if (label != null) {
                        VodovozColorChip(color = label.color, text = label.name)
                    }

                    TimeLeftChip(text = promotionDetails.timeLeft)
                }


                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = AnnotatedString.fromHtml(
                        promotionDetails.description,
                        vodovozTextLinkStyle
                    ) { linkAnnotation -> if(linkAnnotation is LinkAnnotation.Url) onHyperlinkClick(linkAnnotation.url) },
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    modifier = Modifier.padding(top = 32.dp),
                    text = productsTitle,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }



        gridProducts(
            products = products,
            loadState = productsLoadStates,
            shimmerState = shimmerState,
            onProductSee = onProductSee,
            onProductAnalogsClick = onProductAnalogsClick,
            onProductLike = onProductLike,
            onDecrementProductToCart = onDecrementProductToCart,
            onIncrementProductToCart = onIncrementProductToCart,
            onProductClick = onProductClick,
        )

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

