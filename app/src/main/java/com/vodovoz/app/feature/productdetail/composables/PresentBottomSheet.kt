package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vodovoz.app.design_system.composables.bottom_sheet.VodovozDragHandle
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.chip.VodovozColorChip
import com.vodovoz.app.design_system.model.BlockPromoDataUi
import com.vodovoz.app.design_system.model.BuyButtonUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentBottomSheet(
    state: SheetState = rememberModalBottomSheetState(true),
    data: BlockPromoDataUi,
    button: BuyButtonUi,
    onDismissRequest: () -> Unit,
    onBuyButtonClick: () -> Unit,
) {

    val product = data.product

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
                modifier = Modifier.padding(top = 20.dp),
                text = data.title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = data.description,
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .height(78.dp)
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier.size(76.dp)) {
                    AsyncImage(
                        modifier = Modifier.matchParentSize(),
                        model = product.image,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        alignment = Alignment.Center
                    )
                    if (data.productQuantityText.isNotEmpty()) {
                        VodovozColorChip(
                            modifier = Modifier.align(Alignment.BottomEnd),
                            color = MaterialTheme.colorScheme.secondary,
                            text = data.productQuantityText
                        )
                    }
                }
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = product.name,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(
                        modifier = Modifier
                            .wrapContentSize()
                            .height(4.dp)
                    )
                    Row(verticalAlignment = Alignment.Bottom) {

                        Text(
                            text = product.price.new,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val oldPrice = product.price.old
                        if (oldPrice.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(start = 2.dp),
                                text = product.price.old,
                                color = MaterialTheme.colorScheme.surfaceTint,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    textDecoration = TextDecoration.LineThrough
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            VodovozButton(
                modifier = Modifier.padding(bottom = 16.dp),
                text = button.title,
                onClick = onBuyButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = button.backgroundColor.takeOrElse { MaterialTheme.colorScheme.primary },
                    contentColor = button.textColor.takeOrElse { MaterialTheme.colorScheme.background }
                )
            )
        }
    }
}