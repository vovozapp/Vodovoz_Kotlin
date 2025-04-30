package com.vodovoz.app.design_system.composables.placeholders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults
import com.vodovoz.app.design_system.model.VodovozPlaceholderUi

@Composable
fun VodovozLongPlaceholder(
    modifier: Modifier = Modifier,
    data: VodovozPlaceholderUi,
    onCloseClick: () -> Unit = {},
    onButtonClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_close),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.End)
                .clip(CircleShape)
                .clickable(onClick = onCloseClick)
                .padding(16.dp)
                .size(24.dp)
        )

        Spacer(modifier = Modifier.weight(0.75f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(data.imageUrl),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.FillBounds
            )
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = AnnotatedString.fromHtml(data.headerHtml),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center)
            )

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = AnnotatedString.fromHtml(data.descriptionHtml),
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, letterSpacing = 0.sp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        val button = data.button
        if (button != null) {
            VodovozButton(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                text = button.name,
                onClick = onButtonClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = button.textColor,
                    containerColor = button.backgroundColor
                ),
            )
        } else {
            VodovozButton(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                text = stringResource(id = R.string.catalog_button_text),
                onClick = onButtonClick,
                colors = VodovozButtonDefaults.secondaryColors()
            )
        }
    }

}