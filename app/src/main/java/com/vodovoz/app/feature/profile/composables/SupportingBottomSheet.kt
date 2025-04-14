package com.vodovoz.app.feature.profile.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.bottom_sheet.VodovozDragHandle
import com.vodovoz.app.feature.profile.model.ProfileChatItemUi
import com.vodovoz.app.feature.profile.model.ProfileChatsPopupWindowUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportingBottomSheet(
    modifier: Modifier = Modifier,
    data: ProfileChatsPopupWindowUi,
    onItemClick: (ProfileChatItemUi) -> Unit,
    onCopyClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(true),
        onDismissRequest = onDismissRequest,
        dragHandle = {
            VodovozDragHandle()
        },
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    ) {

        SupportingBottomSheetTitle(
            modifier = Modifier.padding(top = 20.dp),
            title = data.title,
            description = data.description,
            onCopyClick = onCopyClick
        )

        SupportingBottomSheetBody(
            chatItems = data.menu,
            onChatItemClick = onItemClick
        )
    }
}

@Composable
private fun SupportingBottomSheetTitle(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onCopyClick: (String) -> Unit,
) {
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_copy),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = { onCopyClick(title) })
        )
    }
}

@Suppress("NonSkippableComposable")
@Composable
private fun SupportingBottomSheetBody(
    modifier: Modifier = Modifier,
    chatItems: List<ProfileChatItemUi>,
    onChatItemClick: (ProfileChatItemUi) -> Unit,
) {
    Column(modifier = modifier) {
        val context = LocalContext.current

        chatItems.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChatItemClick(item) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(item.imageUrl).crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(24.dp),
                    contentScale = ContentScale.FillBounds
                )

                Text(
                    text = item.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (index != chatItems.lastIndex) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}
