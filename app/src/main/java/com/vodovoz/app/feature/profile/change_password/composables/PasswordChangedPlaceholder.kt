package com.vodovoz.app.feature.profile.change_password.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults

@Composable
fun PasswordChangedPlaceholder(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    onFineClick: () -> Unit,
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background).systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_close),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
                .size(24.dp)
                .clip(CircleShape)
                .clickable(onClick = onCloseClick)
        )
        Spacer(modifier = Modifier.weight(1.1f))

        Image(
            painter = painterResource(id = R.drawable.pic_shield),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = stringResource(id = R.string.success_password_change),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(2.2f))

        VodovozButton(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            text = stringResource(R.string.fine),
            onClick = onFineClick,
            colors = VodovozButtonDefaults.secondaryColors()
        )
    }
}