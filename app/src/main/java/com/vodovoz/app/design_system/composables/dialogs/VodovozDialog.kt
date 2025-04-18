package com.vodovoz.app.design_system.composables.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.feature.home.composables.dropShadow

@Composable
fun VodovozDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    acceptButtonText: String,
    cancelButtonText: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
) {
    val shape = MaterialTheme.shapes.large
    val shadowColor = Color.Black

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Column(
            modifier = modifier
                .width(280.dp)
                .dropShadow(shape, shadowColor.copy(0.14f), 24.dp, 16.dp)
                .dropShadow(shape, shadowColor.copy(0.12f), 30.dp, 6.dp)
                .dropShadow(shape, shadowColor.copy(0.20f), 10.dp, 8.dp)
                .background(
                    MaterialTheme.colorScheme.background,
                    shape
                )
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 24.dp,
                    start = 24.dp,
                    end = 32.dp,
                    bottom = 16.dp
                )
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = description,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    modifier = Modifier.height(40.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onClick = onDismiss,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = cancelButtonText,
                        color = MaterialTheme.colorScheme.primary,
                        style = ExtendedTheme.typography.buttonSmall
                    )
                }

                TextButton(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(40.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onClick = onAccept,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = acceptButtonText,
                        color = MaterialTheme.colorScheme.primary,
                        style = ExtendedTheme.typography.buttonSmall
                    )
                }

            }
        }
    }
}
