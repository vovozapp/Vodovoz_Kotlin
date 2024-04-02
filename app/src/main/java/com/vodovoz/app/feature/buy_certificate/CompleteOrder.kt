package com.vodovoz.app.feature.buy_certificate

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI

@Composable
fun CompleteOrder(
    bundle: OrderingCompletedInfoBundleUI,
    onPayUrl: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = 100.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            modifier = Modifier.size(195.dp),
            painter = painterResource(id = R.drawable.png_ordering_complete),
            contentDescription = "png_ordering_complete"
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.spasibo_za_zakaz),
            color = colorResource(id = R.color.text_black),
            fontSize = 16.sp,
            fontFamily = FontFamily(
                Font(R.font.rotonda_normal)
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = bundle.message,
            color = colorResource(id = R.color.text_gray),
            fontSize = 16.sp,
            fontFamily = FontFamily(
                Font(R.font.rotonda_normal)
            ),
        )
        Box(
            modifier = Modifier.weight(1.0f),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    onPayUrl(bundle.paymentURL)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = colorResource(id = R.color.bluePrimary),
                    contentColor = colorResource(id = R.color.white)
                ),
                shape = RoundedCornerShape(5.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.oplatit_text),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(
                        Font(R.font.rotonda_normal)
                    ),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}