package com.vodovoz.app.feature.buy_certificate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vodovoz.app.R
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI

@Composable
fun ChooseCertificateProperty(
    certificatesInfo: BuyCertificatePropertyUI,
    onCertificateSelected: (String) -> Unit,
    onIncreaseCount: () -> Unit,
    onDecreaseCount: () -> Unit,
) {
    PropertyItem(property = certificatesInfo) { property ->
        val fields = property.buyCertificateFieldUIList
        if (!fields.isNullOrEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .wrapContentHeight()
                    .padding(all = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = fields, key = { it.id }) {
                    var modifier = Modifier
                        .size(152.dp, 100.dp)
                        .clickable { onCertificateSelected(it.id) }
                    if (it.isSelected) {
                        modifier = modifier.border(
                            2.dp,
                            colorResource(R.color.accentPrimary),
                            RoundedCornerShape(4.dp)
                        )
                    }
                    Box(
                        modifier = modifier
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxWidth(),
                            model = it.imageUrl.parseImagePath(),
                            contentDescription = it.name
                        )
                    }
                }
            }
            if (property.showAmount) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(modifier = Modifier.width(150.dp)) {
                        Image(
                            modifier = Modifier
                                .height(32.dp)
                                .weight(1.0f)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                )
                                .border(
                                    1.dp,
                                    colorResource(id = R.color.light_gray),
                                    RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                )
                                .clickable {
                                    onDecreaseCount()
                                },
                            painter = painterResource(id = R.drawable.ic_minus_blue),
                            contentDescription = "minus"
                        )
                        Text(
                            modifier = Modifier
                                .height(32.dp)
                                .background(color = Color.White)
                                .weight(1.0f)
                                .border(1.dp, colorResource(id = R.color.light_gray))
                                .wrapContentHeight(),
                            text = property.count.toString(),
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.blackTextDark),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(
                                Font(R.font.rotonda_normal)
                            ),
                            maxLines = 1,
                        )
                        Image(
                            modifier = Modifier
                                .height(32.dp)
                                .weight(1.0f)
                                .background(
                                    color = colorResource(id = R.color.bluePrimary),
                                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = colorResource(id = R.color.light_gray),
                                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                )
                                .clickable {
                                    onIncreaseCount()
                                },
                            painter = painterResource(id = R.drawable.ic_plus_white),
                            contentDescription = "minus"
                        )
                    }
                }
            }
        }
    }
}