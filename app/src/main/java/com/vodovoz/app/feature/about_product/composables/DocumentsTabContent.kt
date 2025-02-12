package com.vodovoz.app.feature.about_product.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vodovoz.app.design_system.model.ContentBlockUi
import com.vodovoz.app.design_system.model.DocumentUi

@Composable
fun DocumentsTabContent(
    modifier: Modifier = Modifier,
    documentsBlock: ContentBlockUi<List<DocumentUi>>,
) {
    Column(modifier = modifier.padding(top = 8.dp, bottom = 16.dp)) {
        documentsBlock.content.forEach { document ->
            DocumentItem(document = document, modifier = Modifier.fillMaxWidth(), onClick = {  })
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
        }
    }
}

@Composable
private fun DocumentItem(document: DocumentUi, modifier: Modifier = Modifier, onClick: (DocumentUi) -> Unit) {
    Row(modifier = modifier.clickable { onClick(document) }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = document.iconUrl,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = document.description,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = document.sizeText,
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}