package com.vodovoz.app.feature.profile.waterapp.composables.user_data

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.feature.profile.waterapp.composables.WaterAppButton

@Composable
fun WaterAppGenderStage(
    modifier: Modifier = Modifier,
    isMan: Boolean,
    onNextClick: () -> Unit,
    onGenderSelect: (isMan: Boolean) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.you_sex),
            modifier = Modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.weight(1.3f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally)
        ) {
            SexItem(
                painter = painterResource(id = R.drawable.pic_man),
                name = stringResource(R.string.man),
                selected = isMan,
                onSelect = { onGenderSelect(true) }
            )
            SexItem(
                painter = painterResource(id = R.drawable.pic_girl),
                name = stringResource(R.string.girl),
                selected = !isMan,
                onSelect = { onGenderSelect(false) }
            )
        }
        Spacer(modifier = Modifier.weight(1.6f))
        WaterAppButton(modifier = Modifier.padding(vertical = 20.dp)) {
            onNextClick()
        }

    }
}

@Composable
private fun SexItem(
    painter: Painter,
    name: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(interactionSource = null, indication = null) { onSelect() }
    ) {
        Box {
            Image(
                alpha = if (selected) 1f else 0.6f,
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(122.dp),
                contentScale = ContentScale.Crop
            )

            if (selected) {
                Image(
                    painter = painterResource(id = R.drawable.pic_checked),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                        .offset(16.dp, (-16).dp)
                )
            }
        }

        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}