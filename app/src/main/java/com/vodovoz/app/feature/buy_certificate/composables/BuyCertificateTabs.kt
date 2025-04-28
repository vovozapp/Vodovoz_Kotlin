package com.vodovoz.app.feature.buy_certificate.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.tab_row.VodovozTabRow
import com.vodovoz.app.feature.buy_certificate.model.BuyCertificateTabUi

@Suppress("NonSkippableComposable")
@Composable
fun BuyCertificateTabs(
    modifier: Modifier = Modifier,
    tabs: List<BuyCertificateTabUi>,
    currentTab: BuyCertificateTabUi,
    onTabClick: (BuyCertificateTabUi) -> Unit,
) {

    val currentTabIndex = tabs.indexOfFirst { it.id == currentTab.id }.takeIf { it != -1 } ?: 0

    VodovozTabRow(
        modifier = modifier
            .height(44.dp)
            .fillMaxWidth(),
        selectedTabPosition = currentTabIndex
    ) {
        tabs.forEachIndexed { index, tab ->
            Text(
                text = tab.name,
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onTabClick(tab) }
                    .wrapContentSize(Alignment.Center)
                    .padding(vertical = 6.dp, horizontal = 2.dp),
                color = if (index == currentTabIndex) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.surfaceTint
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

}