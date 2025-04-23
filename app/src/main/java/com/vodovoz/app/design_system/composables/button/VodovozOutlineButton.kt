package com.vodovoz.app.design_system.composables.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.card.VodovozOutlinedCard

@Composable
fun VodovozOutlineButton(
    modifier: Modifier = Modifier,
    imagePainter: Painter?,
    name: String,
    description: String = "",
    onClick: () -> Unit,
) {
    VodovozOutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(60.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            imagePainter?.let {
                Image(
                    painter = imagePainter,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(40.dp),
                    contentScale = ContentScale.FillBounds
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 0.sp)
                )
                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        color = MaterialTheme.colorScheme.surfaceTint,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClick = onClick),
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surfaceTint
            )
        }
    }

}

@Preview(
    apiLevel = 34
)
@Composable
private fun VodovozOutlineButtonPreview() {
    VodovozTheme {
        VodovozOutlineButton(
            imagePainter = painterResource(id = R.drawable.pic_man),
            name = "S p a s i b o :)",
            onClick = {}
        )
    }
}