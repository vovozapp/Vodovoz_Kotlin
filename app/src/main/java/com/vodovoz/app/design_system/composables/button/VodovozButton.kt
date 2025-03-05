package com.vodovoz.app.design_system.composables.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.ExtendedTheme

@Composable
fun VodovozButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = VodovozButtonDefaults.primaryColors(),
    textStyle: TextStyle = ExtendedTheme.typography.buttonMedium
) {
    FilledTonalButton(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = colors,
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(horizontal = 16.dp),
        enabled = enabled,
        elevation = null
    ) {
        if(isLoading && enabled) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.background,
                trackColor = Color.Transparent
            )
        }else{
            Text(text = text, style = textStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}


@Composable
fun VodovozButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = VodovozButtonDefaults.primaryColors(),
    textStyle: TextStyle = ExtendedTheme.typography.buttonMedium
) {
    FilledTonalButton(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = colors,
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(horizontal = 16.dp),
        enabled = enabled,
        elevation = null
    ) {
        Text(text = text, style = textStyle, maxLines = 1)
    }
}

@Composable
fun VodovozButton(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = VodovozButtonDefaults.primaryColors(),
    textStyle: TextStyle = ExtendedTheme.typography.buttonMedium
) {
    FilledTonalButton(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = colors,
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(horizontal = 16.dp),
        enabled = enabled,
        elevation = null
    ) {
        Text(text = text, style = textStyle, maxLines = 1)
    }
}

@Composable
fun VodovozButtonSmall(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = VodovozButtonDefaults.primaryColors(),
    textStyle: TextStyle = ExtendedTheme.typography.buttonSmall
) {
    FilledTonalButton(
        modifier = modifier
            .height(38.dp)
            .fillMaxWidth(),
        onClick = onClick,
        colors = colors,
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(horizontal = 16.dp),
        enabled = enabled,
        elevation = null
    ) {
        Text(text = text, style = textStyle, maxLines = 1)
    }
}



data object VodovozButtonDefaults {

    @Composable
    fun primaryColors() = ButtonDefaults.filledTonalButtonColors(
        contentColor = MaterialTheme.colorScheme.background,
        disabledContentColor = MaterialTheme.colorScheme.background,
        disabledContainerColor = ExtendedTheme.colorScheme.primaryVariant,
        containerColor = MaterialTheme.colorScheme.primary
    )

    @Composable
    fun secondaryColors() = ButtonDefaults.filledTonalButtonColors(
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )

}