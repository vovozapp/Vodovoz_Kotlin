package com.vodovoz.app.feature.buy_certificate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI

@Composable
fun PropertyItem(
    property: BuyCertificatePropertyUI,
    insideInfo: @Composable (BuyCertificatePropertyUI) -> Unit,
) {
    Text(
        text = if (property.required) {
            property.name + "*"
        } else {
            property.name
        },
        color = if (property.error) {
            colorResource(id = R.color.red)
        } else {
            colorResource(id = R.color.blackTextDark)
        },
        fontSize = 16.sp,
        fontFamily = FontFamily(
            Font(R.font.rotonda_normal)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.light_gray)
            )
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
    )

    insideInfo(property)

    Text(
        text = property.text,
        color = colorResource(id = R.color.blackTextPrimary),
        fontSize = 12.sp,
        fontFamily = FontFamily(
            Font(R.font.rotonda_normal)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.light_gray))
            .padding(
                top = 4.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
    )
}