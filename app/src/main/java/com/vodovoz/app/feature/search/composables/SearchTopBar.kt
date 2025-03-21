package com.vodovoz.app.feature.search.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.top_bar.BasicSearchField
import kotlinx.coroutines.delay

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

    LaunchedEffect(focusRequester) {
        delay(50L)
        focusRequester.requestFocus()
    }


    SearchBaseTopBar(
        modifier = modifier,
        onNavigationClick = onNavigationClick,
        onScanClick = onScanClick
    ) {
        BasicSearchField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = value,
            onValueChange = onValueChange,
            onSearchClick = onSearchClick,
        ) { innerTextField ->
            SearchDecorationBox(
                value = value,
                innerTextField = innerTextField,
                onClearClick = onClearClick,
                onSearchClick = onSearchClick
            )
        }

    }


}

@Composable
private fun SearchBaseTopBar(
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit,
    onScanClick: () -> Unit,
    content: @Composable () -> Unit,
) {
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
                .clip(CircleShape)
                .clickable { onNavigationClick() },
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.width(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            content()
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
private fun SearchDecorationBox(
    value: String,
    innerTextField: @Composable () -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large)
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

@Preview
@Composable
private fun SearchTopBarPreview() {
    VodovozTheme {
        SearchTopBar(
            value = "",
            onValueChange = {},
            onScanClick = { /*TODO*/ },
            onClearClick = {},
            onSearchClick = {},
            onNavigationClick = {}
        )
    }
}