package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.model.CharacteristicUi
import com.vodovoz.app.design_system.model.CharacteristicsBlockUi
import com.vodovoz.app.design_system.model.ContentBlockUi
import kotlinx.coroutines.launch

@Composable
fun ProductDetailsInfo(
    modifier: Modifier = Modifier,
    onAboutProductClick: () -> Unit,
    detailInfo: ContentBlockUi<String>,
    contentBlockCharacteristics: ContentBlockUi<List<CharacteristicsBlockUi>>,
    showDetailText: Boolean,
    onDetailTextSwitch: () -> Unit,
    showAllProperties: Boolean,
    onAllPropertiesShow: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = detailInfo.title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { onAboutProductClick() },
                tint = MaterialTheme.colorScheme.primary
            )
        }


        val animatedRotateFloat by animateFloatAsState(
            targetValue = if (showDetailText) 180f else 0f, label = "arrow down animation"
        )


        val annotatedDetailsInfo =
            AnnotatedString.fromHtml(detailInfo.content.takeWhile { if (!showDetailText) it != '\n' else true })
        if (annotatedDetailsInfo.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = annotatedDetailsInfo,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = if (showDetailText) Int.MAX_VALUE else 4,
                    overflow = TextOverflow.Ellipsis
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(24.dp)
                        .clip(MaterialTheme.shapes.small)
                        .rotate(animatedRotateFloat)
                        .clickable { onDetailTextSwitch() },
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            text = contentBlockCharacteristics.title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )

        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val minCharacteristic = 4
            val commonCharacteristics = (contentBlockCharacteristics.content.firstOrNull()
                ?: CharacteristicsBlockUi.Empty).characteristics


            val showedCharacteristics = commonCharacteristics.take(
                if (showAllProperties) commonCharacteristics.size else minCharacteristic
            )

            showedCharacteristics.forEach { characteristic ->
                CharacteristicItem(
                    modifier = Modifier.fillMaxWidth(),
                    characteristic = characteristic
                )
            }

            if (!showAllProperties && minCharacteristic < commonCharacteristics.size) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(24.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onAllPropertiesShow() },
                    tint = MaterialTheme.colorScheme.surfaceTint
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacteristicItem(modifier: Modifier = Modifier, characteristic: CharacteristicUi) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val coroutineScope = rememberCoroutineScope()

    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Row(modifier = Modifier.weight(1f)) {
            Text(
                modifier = Modifier
                    .weight(1f, false),
                text = buildAnnotatedString {
                    append(characteristic.name)
                    if (!characteristic.hint.isNullOrEmpty()) {
                        appendInlineContent("icon", "[icon]")
                    }
                },
                inlineContent = mapOf(
                    "icon" to InlineTextContent(
                        Placeholder(
                            width = 24.sp,
                            height = 20.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                        )
                    ) {
                        TooltipBox(
                            modifier = Modifier.padding(start = 4.dp),
                            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    shape = MaterialTheme.shapes.small,
                                    tonalElevation = 1.dp,
                                    shadowElevation = 1.dp
                                ) {
                                    Text(
                                        text = characteristic.hint ?: "",
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            state = tooltipState,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_question_circle),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .clickable { coroutineScope.launch { tooltipState.show() } }
                            )
                        }
                    }
                ),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            SelectionContainer {
                Text(
                    text = characteristic.value,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

    }

}

@Preview
@Composable
private fun CharacteristicItemPreview() {
    VodovozTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CharacteristicItem(
                characteristic = CharacteristicUi(
                    1,
                    "",
                    "Вес",
                    "10 кг.",
                    hint = "Вес товара"
                )
            )
        }
    }
}
