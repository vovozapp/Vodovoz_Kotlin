package com.vodovoz.app.feature.buy_certificate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.custom.BuyCertificateTypeUI

@Composable
fun TypeTabs(
    typeList: List<BuyCertificateTypeUI>,
    onTabSelected: (BuyCertificateTypeUI) -> Unit,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

//    val list = listOf("Active", "Completed")

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = colorResource(id = R.color.bluePrimary),
        modifier = Modifier
            .background(color = Color.White)
            .padding(vertical = 4.dp, horizontal = 16.dp)
            .clip(RoundedCornerShape(50))
            .background(color = colorResource(id = R.color.bluePrimary))
            .padding(1.dp),
        indicator = {},
        divider = {},
    ) {
        typeList.forEachIndexed { index, type ->
            val selected = selectedIndex == index
            Tab(
                modifier = if (selected) Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                else Modifier
                    .clip(RoundedCornerShape(50))
                    .background(color = colorResource(id = R.color.bluePrimary)),
                selected = selected,
                onClick = {
                    selectedIndex = index
                    onTabSelected(typeList[index])
                },
                text = {
                    Text(
                        text = type.name,
                        color = if (selected) {
                            colorResource(id = R.color.bluePrimary)
                        } else {
                            Color.White
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(
                            Font(R.font.rotonda_normal)
                        ),
                    )
                }
            )
        }
    }
}