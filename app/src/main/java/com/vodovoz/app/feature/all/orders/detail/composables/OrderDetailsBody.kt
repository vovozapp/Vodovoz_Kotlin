package com.vodovoz.app.feature.all.orders.detail.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.button.VodovozButtonsColumn
import com.vodovoz.app.design_system.composables.decoration.VodovozHorizontalDivider
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.OrderProductUi
import com.vodovoz.app.domain.general.model.order.composables.OrderDetailsButtonUi
import com.vodovoz.app.feature.all.orders.detail.model.OrderDetailsSummaryUi
import com.vodovoz.app.feature.all.orders.detail.model.OrderStatusUi

@Suppress("NonSkippableComposable")
@Composable
fun OrderDetailsBody(
    modifier: Modifier = Modifier,
    currentStatus: OrderStatusUi,
    statuses: List<OrderStatusUi>,
    topButtons: List<OrderDetailsButtonUi>,
    productsTitle: String,
    products: List<OrderProductUi>,
    bottomButtons: List<ColorfulButtonUi>,
    orderSummary: OrderDetailsSummaryUi,
    onTopButtonClick: (OrderDetailsButtonUi) -> Unit,
    onBottomButtonClick: (ColorfulButtonUi) -> Unit,
    onProductClick: (OrderProductUi) -> Unit,
    onProductLike: (OrderProductUi) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (statuses.isNotEmpty()) {
            OrderProgressBar(
                modifier = Modifier.padding(
                    top = 40.dp,
                    bottom = 16.dp,
                    start = 48.dp,
                    end = 48.dp
                ),
                statuses = statuses.map { status -> status.name },
                //todo - put actual step
                currentStep = statuses.size - 1
            )
        }

        OrderDetailsButtonsColumn(
            modifier = Modifier.padding(top = 24.dp),
            topButtons = topButtons,
            onButtonClick = onTopButtonClick
        )

        VodovozHorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (products.isNotEmpty()) {
            OrderDetailsProductColumn(
                title = productsTitle,
                products = products,
                onProductClick = onProductClick,
                onProductLike = onProductLike
            )
        }

        VodovozHorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        OrderDetailsSummaryColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            orderSummary = orderSummary
        )

        VodovozButtonsColumn(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            buttons = bottomButtons,
            onButtonClick = onBottomButtonClick
        )
    }
}