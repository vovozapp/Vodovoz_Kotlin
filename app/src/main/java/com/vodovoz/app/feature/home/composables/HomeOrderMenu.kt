package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.card.VodovozOutlinedCard
import com.vodovoz.app.feature.home.model.MenuItemUi
import com.vodovoz.app.feature.home.model.OrderUi
import com.vodovoz.app.feature.home.model.OrderWithMenuUi

@Composable
fun HomeOrderMenu(
    modifier: Modifier = Modifier,
    orderWithMenu: OrderWithMenuUi,
    onOrderClick: (OrderUi) -> Unit,
    onMenuItemClick: (MenuItemUi) -> Unit,
) {
    val order = orderWithMenu.order
    val menuItems = orderWithMenu.menuItems
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        order?.let {
            item {
                OrderItem(order = order, onClick = onOrderClick)
            }
        }

        items(menuItems) { item ->
            MenuOrderItem(menuItemUi = item, onClick = onMenuItemClick)
        }
    }

}

@Composable
fun MenuOrderItem(
    modifier: Modifier = Modifier,
    menuItemUi: MenuItemUi,
    onClick: (MenuItemUi) -> Unit,
) {
    VodovozOutlinedCard(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 8.dp,
            bottom = 8.dp,
            end = 16.dp
        ),
        onClick = {
            onClick(menuItemUi)
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = menuItemUi.image,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = menuItemUi.title,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = menuItemUi.description,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }

}

@Composable
private fun OrderItem(modifier: Modifier = Modifier, order: OrderUi, onClick: (OrderUi) -> Unit) {
    VodovozOutlinedCard(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        borderColor = MaterialTheme.colorScheme.primary,
        onClick = {
            onClick(order)
        }
    ) {
        Column {
            Row(
                modifier = Modifier.defaultMinSize(minHeight = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_status),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = order.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
            ) {
                Column(modifier = Modifier.defaultMinSize(minWidth = 156.dp)) {
                    Text(
                        text = order.text,
                        color = MaterialTheme.colorScheme.surfaceTint,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(
                            R.string.number_and_text,
                            order.orderId
                        ),
                        color = MaterialTheme.colorScheme.surfaceTint,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = order.price,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }

}



