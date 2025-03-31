package com.vodovoz.app.feature.home.composables

import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.gowtham.ratingbar.RatingBar
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.bottom_sheet.VodovozDragHandle
import com.vodovoz.app.feature.home.model.UnratedProductUi
import com.vodovoz.app.feature.home.model.UnratedProductsSectionUi
import mx.platacard.pagerindicator.PagerWormIndicator


@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
fun UnratedProductsBottomSheet(
    modifier: Modifier = Modifier,
    sectionUnratedProducts: UnratedProductsSectionUi,
    onDispose: () -> Unit,
    onProductRatingChange: (UnratedProductUi, Float) -> Unit,
    onProductRatingChanged: (UnratedProductUi, Float) -> Unit,
) {
    val density = LocalDensity.current

    val partiallyExpandedHeight = with(density) {
        100.dp.toPx()
    }


    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val layoutHeight = constraints.maxHeight.toFloat()

        val anchors = DraggableAnchors {
            SheetValue.Hidden at layoutHeight
            SheetValue.PartiallyExpanded at layoutHeight - partiallyExpandedHeight
            SheetValue.Expanded at 0f
        }


        val state = remember {
            AnchoredDraggableState(
                initialValue = SheetValue.PartiallyExpanded,
                anchors = anchors,
                positionalThreshold = { _ -> 0f },
                velocityThreshold = { 1f },
                snapAnimationSpec = tween(100),
                decayAnimationSpec = exponentialDecay(),
                confirmValueChange = { value ->
                    if (value == SheetValue.PartiallyExpanded) {
                        onDispose()
                    }
                    return@AnchoredDraggableState true
                }
            )
        }

        val shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    val sheetOffsetY = state.requireOffset()
                    IntOffset(x = 0, y = sheetOffsetY.toInt())
                }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Vertical,
                )
                .background(MaterialTheme.colorScheme.background, shape)
                .multiLayersShadow(elevation = 4.dp, shape = shape),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VodovozDragHandle()
            Spacer(modifier = Modifier.height(8.dp))

            SharedTransitionLayout {
                AnimatedContent(
                    targetState = state.currentValue,
                    label = "UpdatedProductsTransition"
                ) { targetState ->
                    when (targetState) {
                        SheetValue.Hidden -> {

                        }

                        SheetValue.Expanded -> {
                            UpdatedProductsExpanded(
                                modifier = Modifier.fillMaxHeight(),
                                animatedVisibilityScope = this@AnimatedContent,
                                title = sectionUnratedProducts.productTitle,
                                products = sectionUnratedProducts.products,
                                onProductRatingChanged = onProductRatingChanged,
                                onNoRateProductClick = {
                                    //TODO
                                }
                            )
                        }

                        SheetValue.PartiallyExpanded -> {
                            UnratedProductsPartially(
                                modifier = Modifier.fillMaxHeight(),
                                anchorDraggableState = state,
                                animatedVisibilityScope = this@AnimatedContent,
                                title = sectionUnratedProducts.title,
                                countProductsText = sectionUnratedProducts.countProductsText,
                                products = sectionUnratedProducts.products
                            )
                        }
                    }

                }
            }
        }
    }
}

fun Modifier.multiLayersShadow(
    elevation: Dp,
    transparencyMultiplier: Float = 0.1f,
    color: Color = Color.Black,
    layers: Int = 10,
    shape: Shape = RoundedCornerShape(8.dp),
): Modifier = this.drawWithCache {

    val shadowSize =
        elevation.toPx() * 1.2f
    val layerSize = shadowSize / layers

    val outline = shape.createOutline(size, layoutDirection, this)
    val path = Path().apply { addOutline(outline) }

    onDrawWithContent {
        repeat(layers) { layer ->
            val layerAlpha = 1f - (1 / layers.toFloat()) * layer
            val reducedLayerAlpha = layerAlpha * transparencyMultiplier

            val scaleFactorX = 1f + (layer * layerSize) / size.width
            val scaleFactorY = 1f + (layer * layerSize) / size.height

            drawIntoCanvas { canvas ->
                canvas.save()

                val centerX = size.width / 2
                val centerY = size.height / 2
                canvas.translate(centerX, centerY)

                canvas.scale(scaleFactorX, scaleFactorY)

                canvas.translate(-centerX, -centerY)

                drawPath(
                    path = path,
                    color = color.copy(alpha = reducedLayerAlpha),
                    style = Stroke(width = layerSize)
                )

                canvas.restore()
            }
        }

        drawContent()
    }
}

fun Modifier.shadowWithClippingBlurMask(
    elevation: Dp,
    shape: Shape = RoundedCornerShape(8.dp),
    spotColor: Color = DefaultShadowColor,
    transparency: Float = 0.25f,
    elevationToBlurMultiplier: Float = 1.2f,
): Modifier = this
    .drawWithCache {
        val transparentColor = spotColor.copy(alpha = transparency)
        val outline = shape.createOutline(size, layoutDirection, this)
        val path = Path().apply { addOutline(outline) }

        val blurRadius = elevation.toPx() * elevationToBlurMultiplier
        val dxPx = 0f  // No horizontal offset for default Material shadow
        val dyPx = (elevation * 0.5f).toPx()  // Vertical offset

        val shadowPaint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = false
                maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                color = transparentColor.toArgb()
            }
        }

        onDrawWithContent {
            drawIntoCanvas { canvas ->
                canvas.save()

                clipPath(path, ClipOp.Difference) {
                    canvas.translate(dxPx, dyPx)  // Apply vertical shadow offset
                    canvas.drawPath(path, shadowPaint)
                }
                canvas.restore()
            }
            drawContent()
        }
    }

@OptIn(ExperimentalSharedTransitionApi::class)
@Suppress("NonSkippableComposable")
@Composable
fun SharedTransitionScope.UpdatedProductsExpanded(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    title: String,
    products: List<UnratedProductUi>,
    onProductRatingChanged: (UnratedProductUi, Float) -> Unit,
    onNoRateProductClick: (UnratedProductUi) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        val pagerState = rememberPagerState { products.size }

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )



        HorizontalPager(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .weight(1f),
            state = pagerState,
            pageSize = PageSize.Fill,
            beyondViewportPageCount = 3,
            key = { page ->
                products[page].id
            },
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            val product = products[page]
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(300.dp)
                        .sharedBounds(
                            rememberSharedContentState(
                                key = "image-key${product.id}"
                            ),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.detailPicture)
                        .placeholderMemoryCacheKey("image-key${product.id}")
                        .memoryCacheKey("image-key${product.id}")
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                )
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = product.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )


                var rating by rememberSaveable {
                    mutableFloatStateOf(0f)
                }

                RatingBar(
                    value = rating,
                    modifier = Modifier.padding(top = 32.dp),
                    painterEmpty = painterResource(id = R.drawable.ic_star_inactive),
                    painterFilled = painterResource(id = R.drawable.ic_star),
                    size = 48.dp,
                    spaceBetween = 6.dp,
                    onValueChange = { newRating ->
                        rating = newRating
                    },
                    onRatingChanged = {
                        onProductRatingChanged(product, it)
                    }
                )
            }
        }

        PagerWormIndicator(
            modifier = Modifier.padding(top = 56.dp, bottom = 12.dp),
            pagerState = pagerState,
            activeDotColor = MaterialTheme.colorScheme.primary,
            dotColor = MaterialTheme.colorScheme.surfaceVariant,
            dotCount = 5,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.do_not_rate_this_product),
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable {
                        onNoRateProductClick(products[pagerState.currentPage])
                    }
                    .padding(12.dp)
            )
        }


    }
}

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Suppress("NonSkippableComposable")
@Composable
fun SharedTransitionScope.UnratedProductsPartially(
    modifier: Modifier = Modifier,
    anchorDraggableState: AnchoredDraggableState<SheetValue>,
    title: String,
    countProductsText: String,
    products: List<UnratedProductUi>,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = countProductsText,
            color = MaterialTheme.colorScheme.surfaceTint,
            style = MaterialTheme.typography.labelMedium
        )

        LazyRow {
            items(products) { product ->
                AsyncImage(
                    modifier = Modifier
                        .size(110.dp)
                        .wrapContentSize()
                        .sharedBounds(
                            rememberSharedContentState(
                                key = "image-key${product.id}"
                            ),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.detailPicture)
                        .placeholderMemoryCacheKey("image-key${product.id}")
                        .memoryCacheKey("image-key${product.id}")
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                )
            }
        }
    }
}