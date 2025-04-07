package com.vodovoz.app.feature.search.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R

@Composable
fun SearchEmptyPlaceholder(
    modifier: Modifier = Modifier,
    imagePainter: Painter,
    description: String,
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 62.dp)
                .size(80.dp)
        )
        Text(
            modifier = Modifier
                .padding(top = 24.dp)
                .padding(horizontal = 32.dp),
            text = AnnotatedString.fromHtml(description),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
    }
}