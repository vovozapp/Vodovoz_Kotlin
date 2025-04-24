package com.vodovoz.app.design_system.composables.placeholders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults
import com.vodovoz.app.design_system.model.ErrorDataUi

@Composable
fun ErrorDataPlaceholder(modifier: Modifier = Modifier, errorData: ErrorDataUi, onButtonClick: () -> Unit = {}) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(errorData.imageUrl),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.FillBounds
            )
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = AnnotatedString.fromHtml(errorData.headerHtml),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center)
            )

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = AnnotatedString.fromHtml(errorData.descriptionHtml),
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
            )
        }

        val button = errorData.button
        if(button != null){
            VodovozButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp),
                text = button.name,
                onClick = onButtonClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = button.textColor,
                    containerColor = button.backgroundColor
                ),
            )
        }
        else {
            VodovozButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp),
                text = stringResource(id = R.string.catalog_button_text),
                onClick = onButtonClick,
                colors = VodovozButtonDefaults.secondaryColors()
            )
        }



        Spacer(modifier = Modifier.weight(1.2f))
    }

}