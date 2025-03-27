package com.vodovoz.app.feature.profile.userdata.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.R

@Composable
fun UserDataPhotoColumn(
    modifier: Modifier = Modifier,
    photo: String,
    photoTitle: String,
    photoDescription: String,
    onPhotoClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .crossfade(true)
                    .data(photo)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable { onPhotoClick() },
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.pic_avatar),
                error = painterResource(id = R.drawable.pic_avatar)
            )
            Image(
                painter = painterResource(id = R.drawable.icon_camera),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp),
                contentScale = ContentScale.FillBounds
            )
        }

        Text(
            modifier = Modifier.padding(start = 36.dp, end = 36.dp, top = 16.dp),
            text = photoTitle,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(start = 80.dp, end = 80.dp, top = 4.dp),
            text = photoDescription,
            color = MaterialTheme.colorScheme.surfaceTint,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center
        )
    }

}