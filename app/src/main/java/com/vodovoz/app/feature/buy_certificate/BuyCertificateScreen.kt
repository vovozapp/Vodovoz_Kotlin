package com.vodovoz.app.feature.buy_certificate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.custom.BuyCertificateBundleUI

@Composable
fun BuyCertificateScreen(
    state: BuyCertificateBundleUI,
    viewModel: BuyCertificateViewModel,
) {

    val listLastItemBottomArrangement = object : Arrangement.Vertical {
        override fun Density.arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
            val consumedSize = sizes.fold(0) { a, b -> a + b }
            val remainingSize = totalSize - consumedSize

            var current = 0
            sizes.forEachIndexed() { index, it ->
                if (index == sizes.lastIndex) {
                    // Move last item to bottom
                    outPositions[index] = current + remainingSize
                } else {
                    outPositions[index] = current
                    current += it
                }
            }
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = colorResource(
                    id = R.color.light_gray
                )
            ),
        verticalArrangement = listLastItemBottomArrangement
    ) {

        if (state.certificateInfo != null) {
            item {
                ChooseCertificateProperty(
                    certificatesInfo = state.certificateInfo,
                    onCertificateSelected = { id ->
                        viewModel.selectCertificate(id)
                    },
                    onIncreaseCount = {
                        viewModel.increaseCount()
                    },
                    onDecreaseCount = {
                        viewModel.decreaseCount()
                    }
                )
            }
        }

        if (!state.typeList.isNullOrEmpty()) {
            item {
                TypeTabs(typeList = state.typeList,
                    onTabSelected = {
                        viewModel.selectType(it)
                    }
                )
            }
        }

        val properties =
            state.typeList?.firstOrNull { it.isSelected }?.buyCertificatePropertyList
        if (!properties.isNullOrEmpty()) {
            items(count = properties.size, key = { properties[it].code }) {
                if (properties[it].code.contains("email")) {
                    PropertyEmail(
                        properties[it],
                        onValueChange = { value ->
                            viewModel.addResult(properties[it].code, value)
                        }
                    )
                }
                if (properties[it].code.contains("opisanie")) {
                    PropertyMessage(property = properties[it],
                        onValueChange = { value ->
                            viewModel.addResult(properties[it].code, value)
                        }
                    )
                }
            }
        }

        item {
            PropertyPayment(
                paymentUI = state.payment,
                onClick = {
                    viewModel.showPaymentMethods()
                }
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
                    .background(
                        color = colorResource(id = R.color.light_gray)
                    )
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.buyCertificate()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = colorResource(id = R.color.bluePrimary),
                        contentColor = colorResource(id = R.color.white)
                    ),
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = "Купить",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(
                            Font(R.font.rotonda_normal)
                        )
                    )
                }
            }
        }
    }
}