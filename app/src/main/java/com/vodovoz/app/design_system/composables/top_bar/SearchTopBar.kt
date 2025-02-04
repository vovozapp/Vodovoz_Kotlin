package com.vodovoz.app.design_system.composables.top_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onFocus: () -> Unit,
    onMicClick: () -> Unit,
    onScanClick: () -> Unit,
    onSearchClick: () -> Unit,
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = modifier
            .height(54.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                onClick = onSearchClick,
                                interactionSource = null,
                                indication = null
                            ),
                        painter = painterResource(id = R.drawable.icon_search),
                        tint = MaterialTheme.colorScheme.surfaceTint,
                        contentDescription = null
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .weight(1f)
                    ) {
                        if (value.isBlank()) {
                            Text(
                                text = stringResource(R.string.search_product),
                                color = MaterialTheme.colorScheme.surfaceTint,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        innerTextField()
                    }
                    IconButton(onClick = onMicClick, modifier = Modifier.size(48.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mic),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = onScanClick, modifier = Modifier.size(48.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_scan),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        )
    }

    LaunchedEffect(isFocused) {
        if (isFocused) onFocus()
    }

}

@Preview
@Composable
private fun SearchTopBarPreview() {
    VodovozTheme {
        SearchTopBar(
            value = "",
            onValueChange = {},
            onFocus = { },
            onMicClick = {},
            onScanClick = { /*TODO*/ },
            onSearchClick = {}
        )
    }
}