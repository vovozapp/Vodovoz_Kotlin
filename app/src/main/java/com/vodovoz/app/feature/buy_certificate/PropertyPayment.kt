package com.vodovoz.app.feature.buy_certificate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.custom.BuyCertificatePaymentUI

@Composable
fun PropertyPayment(
    paymentUI: BuyCertificatePaymentUI,
    onClick: () -> Unit,
) {
    Text(
        text = if (paymentUI.required) {
            paymentUI.name + "*"
        } else {
            paymentUI.name
        },
        color = if (paymentUI.error) {
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
    val paymentMethod = paymentUI.payMethods.firstOrNull { it.isSelected }
    Text(
        text = paymentMethod?.name ?: "Выберите способ оплаты",
        color = if (paymentMethod != null) {
            colorResource(id = R.color.blackTextDark)
        } else {
            colorResource(id = R.color.blackTextPrimary)
        },
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(
            Font(R.font.rotonda_normal)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(all = 16.dp)
            .clickable { onClick() },
    )


}