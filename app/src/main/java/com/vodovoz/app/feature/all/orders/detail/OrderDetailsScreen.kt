package com.vodovoz.app.feature.all.orders.detail

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.feature.all.orders.detail.composables.OrderDetailsBody
import com.vodovoz.app.feature.all.orders.detail.composables.OrderDetailsTopBar

@Suppress("NonSkippableComposable")
@Composable
fun OrderDetailsScreen(
    viewModel: OrderDetailsFlowViewModel,
    viewState: OrderDetailsFlowViewModel.OrderDetailsState,
) {
    Scaffold(
        topBar = {
            OrderDetailsTopBar(
                title = viewState.title,
                subtitle = viewState.subtitle,
                onBack = {
                    viewModel.navigateBack()
                },
                onCopy = {
                    viewModel.copyOrderId()
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        OrderDetailsBody(
            modifier = Modifier.padding(padding),
            statuses = viewState.statuses,
            currentStatus = viewState.currentStatus,
            topButtons = viewState.topButtons,
            productsTitle = viewState.productsTitle,
            products = viewState.products,
            bottomButtons = viewState.bottomButtons,
            orderSummary = viewState.orderSummary,
            onTopButtonClick = { orderDetailsButton ->
                viewModel.activateTopButton(orderDetailsButton)
            },
            onBottomButtonClick = { button ->
                viewModel.activateBottomButton(button)
            },
            onProductLike = { orderProduct ->
                viewModel.changeProductFavorite(orderProduct)
            },
            onProductClick = { orderProduct ->
                viewModel.navigateToProductDetails(orderProduct)
            }
        )
    }
}