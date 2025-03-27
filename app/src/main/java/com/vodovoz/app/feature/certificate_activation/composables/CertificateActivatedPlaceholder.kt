package com.vodovoz.app.feature.certificate_activation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults
import com.vodovoz.app.design_system.vodovozTextLinkStyle

@Composable
fun CertificateActivatedPlaceholder(
    modifier: Modifier = Modifier,
    message: String,
    onCloseClick: () -> Unit,
    onOkClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_close),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onCloseClick() },
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.weight(1f))


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = R.drawable.pic_done),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = AnnotatedString.fromHtml(message, vodovozTextLinkStyle),
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

        }

        Spacer(modifier = Modifier.weight(2f))


        VodovozButton(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            text = stringResource(R.string.its_clear),
            onClick = { onOkClick() },
            colors = VodovozButtonDefaults.secondaryColors()
        )
    }
}