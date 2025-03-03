package com.vodovoz.app.design_system.composables.top_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import kotlinx.coroutines.delay
import kotlin.math.abs


@Composable
fun SearchTopBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onScanClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNavigationClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(50L)
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .height(54.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(
                    CircleShape
                )
                .clickable { onNavigationClick() },
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.width(16.dp))

        BasicSearchField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            value = value,
            onValueChange = onValueChange,
            onSearchClick = onSearchClick,
        ) { innerTextField ->
            TextFieldDefaults.SearchDecorationBox(
                value = value,
                innerTextField = innerTextField,
                onClearClick = onClearClick,
                onSearchClick = onSearchClick
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(onClick = onScanClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_scan),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

    }
}

@Composable
fun StaticSearchTopBar(
    modifier: Modifier = Modifier,
    value: String = "",
    onFocus: () -> Unit,
    onMicClick: () -> Unit,
    onScanClick: () -> Unit,
    onNavigationClick: () -> Unit,
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = modifier
            .height(54.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onNavigationClick() },
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.width(16.dp))


        BasicSearchField(
            value = value,
            onValueChange = {},
            interactionSource = interactionSource,
            onSearchClick = {},
            readOnly = true
        ) { innerTextField ->
            TextFieldDefaults.StaticSearchDecorationBox(
                value = value,
                innerTextField = innerTextField,
                onMicClick = onMicClick,
                onScanClick = onScanClick
            )
        }


    }

    LaunchedEffect(isFocused) {
        if (isFocused) onFocus()
    }

}

@Composable
private fun TextFieldDefaults.SearchDecorationBox(
    value: String,
    innerTextField: @Composable () -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onSearchClick() },
            painter = painterResource(id = R.drawable.icon_search),
            tint = MaterialTheme.colorScheme.surfaceTint,
            contentDescription = null
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
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

            if (value.isNotBlank()) {
                IconButton(onClick = onClearClick, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clean),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

        }
    }

}

@Composable
private fun TextFieldDefaults.StaticSearchDecorationBox(
    value: String,
    innerTextField: @Composable () -> Unit,
    onMicClick: () -> Unit,
    onScanClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
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

@Composable
private fun BasicSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() },
) {
    var textField by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    LaunchedEffect(value) {
        if (abs(textField.text.length - value.length) >= 2) {
            textField = textField.copy(text = value, selection = TextRange(value.length))
            return@LaunchedEffect
        } else textField = textField.copy(text = value)
    }

    BasicTextField(
        value = textField,
        onValueChange = { newValue ->
            textField = newValue
            onValueChange(newValue.text)
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onBackground),
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large),
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        interactionSource = interactionSource,
        decorationBox = decorationBox,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearchClick() }
        ),
        readOnly = readOnly,
    )

}

@Preview
@Composable
private fun StaticSearchTopBarPreview() {
    VodovozTheme {
        StaticSearchTopBar(
            value = "",
            onFocus = { },
            onMicClick = {},
            onScanClick = { /*TODO*/ },
            onNavigationClick = {}
        )
    }
}

@Preview
@Composable
private fun SearchTopBarPreview() {
    VodovozTheme {
        SearchTopBar(
            value = "dwqdqw",
            onValueChange = {},
            onScanClick = { /*TODO*/ },
            onClearClick = {},
            onSearchClick = {},
            onNavigationClick = {}
        )
    }
}