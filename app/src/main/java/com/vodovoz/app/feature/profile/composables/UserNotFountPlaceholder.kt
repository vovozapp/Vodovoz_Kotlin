package com.vodovoz.app.feature.profile.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.model.ColorfulButtonUi

@Composable
fun UserNotFountPlaceholder(
    modifier: Modifier = Modifier,
    title: String,
    header: String,
    description: String,
    image: String,
    button: ColorfulButtonUi,
    onButtonClick: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.height(56.dp).fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.weight(0.6f))

        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier.size(80.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )


            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = header,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = description,
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

        }

        VodovozButton(
            modifier = Modifier.padding(top = 42.dp, start = 16.dp, end = 16.dp),
            text = button.name,
            onClick = { onButtonClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = button.backgroundColor.takeOrElse {
                    MaterialTheme.colorScheme.primary
                },
                contentColor = button.textColor.takeOrElse {
                    MaterialTheme.colorScheme.onBackground
                }
            )
        )

        Spacer(modifier = Modifier.weight(1.5f))
    }
}