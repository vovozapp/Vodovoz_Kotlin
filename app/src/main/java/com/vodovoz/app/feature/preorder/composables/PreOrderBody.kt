package com.vodovoz.app.feature.preorder.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.snackbar.VodovozSnackbarHost
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextFieldsColumn
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.feature.preorder.model.FieldUi

@Suppress("NonSkippableComposable")
@Composable
fun PreOrderBody(
    modifier: Modifier = Modifier,
    colorfulButton: ColorfulButtonUi,
    fields: List<FieldUi>,
    snackbarHostState: SnackbarHostState,
    onOrderSend: () -> Unit,
    onFieldValueChange: (FieldUi, String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp)
        ) {

            VodovozTextFieldsColumn(
                fields = fields,
                onFieldChange = { field, updatedField ->
                    onFieldValueChange(field, updatedField.value)
                },
                onDone = {}
            )

            VodovozButton(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                text = colorfulButton.name,
                onClick = {
                    onOrderSend()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorfulButton.backgroundColor,
                    contentColor = colorfulButton.textColor
                )
            )

        }

        VodovozSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

}
