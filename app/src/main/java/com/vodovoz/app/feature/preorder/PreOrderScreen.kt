package com.vodovoz.app.feature.preorder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.feature.preorder.composables.PreOrderBody
import com.vodovoz.app.feature.preorder.composables.PreOrderTopBar

@Suppress("NonSkippableComposable")
@Composable
fun PreOrderScreen(
    viewModel: PreOrderFlowViewModel,
    viewState: PreOrderFlowViewModel.PreOrderState,
    snackbarHostState: SnackbarHostState
) {

    val sectionPreOrder = viewState.sectionPreOrder
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars)

    ) {
        PreOrderTopBar(title = sectionPreOrder.title, onCloseClick = { viewModel.navigateBack() })

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