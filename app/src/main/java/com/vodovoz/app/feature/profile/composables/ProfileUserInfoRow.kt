package com.vodovoz.app.feature.profile.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.R
import com.vodovoz.app.feature.profile.model.UserInfoBlockUi

@Composable
fun ProfileUserInfoRow(
    modifier: Modifier = Modifier,
    userInfoBlock: UserInfoBlockUi,
    onClick: () -> Unit,
) {
    Row(modifier = modifier.clickable { onClick() }.padding(horizontal = 16.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .crossfade(true)
                .crossfade(100)
                .data(userInfoBlock.imageUrl)
                .build(),
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.pic_avatar),
            modifier = Modifier
                .padding(vertical = 3.dp)
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.FillBounds,
            error = painterResource(id = R.drawable.pic_avatar)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(
                text = userInfoBlock.username,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )

            val textColor =
                userInfoBlock.textButton.textColor.takeOrElse { MaterialTheme.colorScheme.surfaceTint }

            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userInfoBlock.textButton.text,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )

                Icon(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = textColor
                )
            }
        }
    }
}