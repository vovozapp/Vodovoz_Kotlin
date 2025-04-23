package com.vodovoz.app.feature.about_product.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.model.CharacteristicsBlockUi
import com.vodovoz.app.design_system.model.ContentBlockUi
import com.vodovoz.app.design_system.composables.decoration.VodovozHorizontalDivider
import com.vodovoz.app.feature.productdetail.composables.CharacteristicItem

@Composable
fun CharacteristicsTabContent(
    modifier: Modifier = Modifier,
    characteristics: ContentBlockUi<List<CharacteristicsBlockUi>>,
) {
    Column(modifier = modifier.padding(vertical = 16.dp)) {
        characteristics.content.forEach { block ->
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = block.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                block.characteristics.forEach { characteristic ->
                    CharacteristicItem(characteristic = characteristic)
                }
            }

            if (characteristics.content.indexOf(block) != characteristics.content.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
                VodovozHorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}