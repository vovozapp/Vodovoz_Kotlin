package com.vodovoz.app.feature.preorder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.feature.preorder.composables.PreOrderBody
import com.vodovoz.app.design_system.composables.top_bar.ClosingTopBar

@Suppress("NonSkippableComposable")
@Composable
fun PreOrderScreen(
    viewModel: PreOrderFlowViewModel,
    viewState: PreOrderFlowViewModel.PreOrderState,
    snackbarHostState: SnackbarHostState
) {

    val sectionPreOrder = viewState.sectionPreOrder
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)

    ) {
        ClosingTopBar(title = sectionPreOrder.title, onCloseClick = { viewModel.navigateBack() })

        PreOrderBody(
            colorfulButton = sectionPreOrder.colorfulButton,
            fields = sectionPreOrder.fields,
            snackbarHostState = snackbarHostState,
            onOrderSend = { viewModel.sendPreOrder() },
            onFieldValueChange = { field, newValue ->
                viewModel.changeFieldValue(field, newValue)
            }
        )

    }
}