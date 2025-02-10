package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.domain.general.model.ButtonAction
import com.vodovoz.app.feature.home.model.ButtonUi

@Composable
fun TitleAndButton(
    modifier: Modifier = Modifier,
    title: String,
    button: ButtonUi? = null,
    onShowAllClick: (ButtonAction) -> Unit = {},
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )

        button?.let {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onShowAllClick(button.action) }
                    .padding(start = 10.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.all),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeTitleAndAllPreview() {
    VodovozTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            TitleAndButton(title = "Популярные разделы") {

            }
        }
    }
}