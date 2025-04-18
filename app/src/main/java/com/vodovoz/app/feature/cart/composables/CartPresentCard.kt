package com.vodovoz.app.feature.cart.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.design_system.composables.button.VodovozButtonSmall
import com.vodovoz.app.feature.cart.model.CartPresentUi

@Composable
fun CartPresentCard(
    modifier: Modifier = Modifier,
    currentCartPrice: Int,
    present: CartPresentUi,
    onChoosePresentClick: () -> Unit,
) {
    val context = LocalContext.current

    BoxWithConstraints {
        val maxWidth = maxWidth
        val horizontalPadding = 16.dp

        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(
                    horizontal = horizontalPadding,
                    vertical = 10.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .zIndex(1f)
            ) {
                Text(
                    modifier = Modifier.padding(end = 30.dp),
                    text = present.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    ),
                )
                Text(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .wrapContentWidth(Alignment.Start, true)
                        .fillMaxWidth(),
                    text = AnnotatedString.fromHtml(present.description),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )

                val button = present.button
                if (button != null) {
                    VodovozButtonSmall(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .wrapContentWidth(Alignment.Start, true),
                        onClick = onChoosePresentClick,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = button.textColor.takeOrElse { MaterialTheme.colorScheme.onBackground },
                            containerColor = button.backgroundColor.takeOrElse { MaterialTheme.colorScheme.primary }
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                modifier = Modifier,
                                text = button.name,
                                style = ExtendedTheme.typography.buttonSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_right),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(20.dp)
                            )
                        }
                    }
                } else {
                    val animatedProgress by animateFloatAsState(
                        targetValue = currentCartPrice.toFloat() / present.maxPresentPrice,
                        label = "animated present progress",
                        animationSpec = tween(160)
                    )

                    Row(
                        modifier = Modifier
                            .wrapContentWidth(Alignment.Start, true)
                            .width(maxWidth - (horizontalPadding + horizontalPadding)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .weight(1f)
                                .height(8.dp)
                                .clip(MaterialTheme.shapes.small),
                            strokeCap = StrokeCap.Square,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.background,
                            progress = { animatedProgress },
                            drawStopIndicator = {}

                        )


                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(20.dp)
                        )
                    }

                }
            }

            AsyncImage(
                modifier = Modifier
                    .heightIn(max = 80.dp)
                    .widthIn(max = 80.dp)
                    .zIndex(0f),
                model = ImageRequest.Builder(context).data(present.image).crossfade(true).build(),
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.pic_heart), //todo - replace to present
                error = painterResource(id = R.drawable.pic_heart), //todo - replace to present
                contentScale = ContentScale.Inside
            )
        }
    }
}