package com.vodovoz.app.design_system.composables.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.model.ColorfulButtonUi

@Suppress("NonSkippableComposable")
@Composable
fun VodovozButtonsColumn(
    buttons: List<ColorfulButtonUi>,
    modifier: Modifier = Modifier,
    onButtonClick: (ColorfulButtonUi) -> Unit,
) {


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        buttons.forEach { button ->
            val contentColor =
                button.backgroundColor.takeOrElse { MaterialTheme.colorScheme.primary }
            val containerColor =
                button.textColor.takeOrElse { MaterialTheme.colorScheme.background }

            VodovozButton(
                text = button.name,
                onClick = { onButtonClick(button) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = contentColor,
                    contentColor = containerColor,
                    disabledContentColor = contentColor.copy(0.4f),
                    disabledContainerColor = contentColor.copy(0.4f)
                ),
                enabled = button.enabled,
                isLoading = button.loading,
            )
        }
    }

}