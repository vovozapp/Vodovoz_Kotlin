package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R

@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsSearchWords(modifier: Modifier = Modifier, searchWords: List<String>) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.search_by_queries),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            searchWords.forEach { searchWord ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        text = searchWord,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}