package com.vodovoz.app.feature.cart.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.bottom_sheet.VodovozDragHandle
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextField
import com.vodovoz.app.feature.cart.model.CartPromoPopupWindowUi
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionCodeBottomSheet(
    modifier: Modifier = Modifier,
    info: CartPromoPopupWindowUi,
    promoCode: String,
    onPromoCodeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onApplyPromoClick: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = {
            onDismiss()
        },
        shape = MaterialTheme.shapes.large.copy(
            bottomEnd = CornerSize(0.dp),
            bottomStart = CornerSize(0.dp)
        ),
        sheetState = rememberModalBottomSheetState(true),
        dragHandle = {
            VodovozDragHandle()
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        val requester = remember { FocusRequester() }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 20.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Text(
                text = info.title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )

            VodovozTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .focusable()
                    .focusRequester(requester),
                value = promoCode,
                onValueChange = onPromoCodeChange,
                hint = info.fieldHint,
                trailingIcon = if (promoCode.isNotEmpty()) {
                    @Composable {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clean),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onPromoCodeChange("")
                                },
                            tint = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                } else null,
                isError = info.errorText != null,
                supportingText = info.errorText
            )

            VodovozButton(
                modifier = Modifier.padding(top = 16.dp),
                text = info.buttonName,
                onClick = onApplyPromoClick,
                enabled = promoCode.isNotBlank(),
            )
        }


        LaunchedEffect(requester) {
            delay(200L)
            requester.requestFocus()
        }
    }
}