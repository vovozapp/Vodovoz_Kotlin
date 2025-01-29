package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.ui.model.PropertyUI
import kotlinx.coroutines.delay

@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsInfo(
    modifier: Modifier = Modifier,
    onAboutProductClick: () -> Unit,
    previewText: String,
    detailPreviewText: String,
    showDetailPreviewText: Boolean,
    onDetailPreviewTextShowOrHide: () -> Unit,
    properties: List<PropertyUI>,
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
                text = stringResource(R.string.about_product),
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
            targetValue = if (showDetailPreviewText) 180f else 0f, label = "arrow down animation"
        )

        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .animateContentSize(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = if (showDetailPreviewText) {
                    HtmlCompat.fromHtml(detailPreviewText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        .toString()
                } else {
                    HtmlCompat.fromHtml(previewText, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                        .takeWhile { it != '\n' }
                },
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .rotate(animatedRotateFloat)
                    .clickable { onDetailPreviewTextShowOrHide() },
                tint = MaterialTheme.colorScheme.surfaceTint
            )
        }

        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.Characteristic),
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
            properties
                .take(if (showAllProperties) properties.size else 4)
                .forEach { characteristic ->
                    CharacteristicItem(name = characteristic.name, value = characteristic.value)
                }

            if (!showAllProperties) {
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

@Composable
private fun CharacteristicItem(modifier: Modifier = Modifier, name: String, value: String) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        //todo - put question icon
        Text(
            modifier = Modifier.weight(1f),
            text = name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1.05f),
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsInfoPreview() {
    VodovozTheme {

        val productCharacteristics = listOf(
            PropertyUI(name = "Объем", value = "11.5 литров"),
            PropertyUI(name = "Производитель", value = "Старо-Мытищинский Источник"),
            PropertyUI(name = "Тип воды", value = "Питьевая"),
            PropertyUI(name = "Срок годности", value = "6 месяцев"),
            PropertyUI(name = "Залоговая стоимость за бутыль", value = "400 ₽"),
            PropertyUI(name = "Общая минерализация", value = "0,05-0,5 мг/л"),
            PropertyUI(name = "Общая жесткость", value = "0-2,5 мг/л"),
            PropertyUI(name = "Калий", value = "0,1-10 мг/л"),
            PropertyUI(name = "Кальций", value = "0,1-30 мг/л"),
            PropertyUI(name = "Магний", value = "0,1-20 мг/л"),
            PropertyUI(name = "Хлориды", value = "0,1-10 мг/л"),
            PropertyUI(name = "Сульфаты", value = "1-40 мг/л"),
            PropertyUI(name = "Вес упаковки", value = "12 кг"),
            PropertyUI(name = "Размер упаковки (ШxГxВ)", value = "0.24 м x 0.24 м x 0.42 м"),
            PropertyUI(name = "Для кулера", value = "Да"),
            PropertyUI(name = "Штрихкод", value = "4603727231335")
        )

        var show by remember {
            mutableStateOf(false)
        }
        var showCharacteristics by remember {
            mutableStateOf(false)
        }

        ProductDetailsInfo(
            onAboutProductClick = {
            },
            previewText = """
        Природная вода «Старо-Мытищинский источник» сохраняет свои природные вкус и свежесть. 
        Рекомендуется для ежедневного употребления, подходит для приготовления пищи, 
        не оставляет накипи при кипячении и не даёт осадка. Это идеальная вода для активных людей.
    """.trimIndent(),
            detailPreviewText = """
        Природная вода «Старо-Мытищинский источник» сохраняет свои природные вкус и свежесть. 
        Рекомендуется для ежедневного употребления, подходит для приготовления пищи, 
        не оставляет накипи при кипячении и не даёт осадка. Это идеальная вода для активных людей.
                
        Вода добывается в экологически чистом районе, из скважины глубиной 110 метров.
        В процессе производства вода прошла многоступенчатую фильтрацию, сохраняя все минеральные свойства природной воды. 
        Рекомендуемая температура хранения воды — от +5 до +25 градусов.
    """.trimIndent(),
            showDetailPreviewText = show,  // Начальное состояние: детальный текст скрыт
            onDetailPreviewTextShowOrHide = {

            },
            showAllProperties = showCharacteristics,
            properties = productCharacteristics,
            onAllPropertiesShow = {

            }
        )

        LaunchedEffect(key1 = Unit) {
            while (true) {
                delay(5000L)
                show = !show
                delay(500L)
                showCharacteristics = !showCharacteristics
            }
        }
    }
}