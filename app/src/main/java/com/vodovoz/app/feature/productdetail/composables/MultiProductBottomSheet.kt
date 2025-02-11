package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.design_system.composables.bottom_sheet.VodovozDragHandle
import com.vodovoz.app.design_system.composables.button.BaseQuantityButton
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.tab_row.VodovozTabRow
import com.vodovoz.app.design_system.model.PriceUi
import com.vodovoz.app.util.calculateProductPrice
import com.vodovoz.app.util.extensions.indexOfOrNull
import kotlin.math.roundToInt

@Suppress("NonSkippableComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiProductBottomSheet(
    state: SheetState = rememberModalBottomSheetState(true),
    cartQuantity: Int,
    firstPrice: PriceUi,
    prices: List<PriceUi>,
    buttonIsLoading: Boolean,
    onDismissRequest: () -> Unit,
    onCartQuantityChange: (Int) -> Unit,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
) {
    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismissRequest,
        dragHandle = {
            VodovozDragHandle()
        },
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = stringResource(R.string.choose_quantity),
                modifier = Modifier.padding(top = 20.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )

            val selectedPriceIndex =
                prices.indexOfOrNull(prices.sortedByDescending { it.quantityTo }
                    .firstOrNull { cartQuantity >= it.quantityFrom }) ?: 0

            if (prices.isNotEmpty()) {
                VodovozTabRow(
                    modifier = Modifier.padding(top = 16.dp),
                    selectedTabIndex = selectedPriceIndex,
                    spacing = 8.dp
                ) {
                    prices.forEach { price ->
                        VodovozChip(
                            text = stringResource(R.string.from_quantity, price.quantityFrom),
                            selected = price.quantityFrom <= cartQuantity,
                            onSelect = {
                                onCartQuantityChange(price.quantityFrom.coerceAtLeast(1))
                            }
                        )
                    }
                }
            }

            val calculatedPrice = calculateProductPrice(cartQuantity, prices).roundToInt()


            Text(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.CenterHorizontally),
                text = stringResource(
                    R.string.price,
                    calculatedPrice
                ),
                color = if (!buttonIsLoading) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                style = MaterialTheme.typography.headlineMedium
            )

            val savingPrice =
                ((firstPrice.price * cartQuantity) - calculatedPrice).roundToInt()
                    .coerceAtLeast(0)
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.CenterHorizontally),
                text = stringResource(R.string.saving_price, savingPrice),
                color = if (savingPrice > 0) MaterialTheme.colorScheme.secondary else if (buttonIsLoading) Color.Transparent else MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.labelSmall
            )

            BaseQuantityButton(
                modifier = Modifier.padding(top = 20.dp),
                isLoading = buttonIsLoading,
                onPlus = onPlus,
                onMinus = onMinus,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = ExtendedTheme.colorScheme.primaryVariant
                ),
                minusEnabled = cartQuantity > 1
            ) {
                if (buttonIsLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = cartQuantity.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        style = ExtendedTheme.typography.buttonMedium
                    )
                }
            }

            VodovozButton(
                modifier = Modifier.padding(vertical = 16.dp),
                text = stringResource(id = R.string.to_cart),
                onClick = onDismissRequest
            )
        }
    }
}