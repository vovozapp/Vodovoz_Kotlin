package com.vodovoz.app.composable.common_views

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.model.ProductUI

@SuppressLint("ComposableNaming")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductGridView(item: Item): Dp {
    val productUI = item as ProductUI
    val pagerState = rememberPagerState(pageCount = {productUI.detailPictureList.size})
    val detailPictureList = productUI.detailPictureList
    val localDensity = LocalDensity.current

    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp, end = 4.dp, bottom = 4.dp)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(12.dp),
                color = colorResource(id = R.color.border_blue)
            )
            //.onGloballyPositioned { coordinates ->
            //    columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
            //}
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(8.dp)
        ) {
            ImagePart(detailPictureList = detailPictureList, pagerState = pagerState)
            PagingIndicator(pagerState)
            TextPart()
            BasketButton()
        }
    }
    return columnHeightDp
}

@OptIn(ExperimentalFoundationApi::class)
@Suppress("NonSkippableComposable")
@Composable
fun ImagePart(detailPictureList: List<String>, pagerState: PagerState){
    Box(
        modifier = Modifier.aspectRatio(1f)
    ) {
        HorizontalPager(
            modifier = Modifier,
            state = pagerState,
            key = { detailPictureList[it] }
        ) { index ->
            SetImage(
                image = detailPictureList[index],
                modifier = Modifier,
                shape = RoundedCornerShape(12.dp),
                contentScale = ContentScale.Crop
            )
        }
        Column (
        ){
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                PromoTextCell(text = "-15%", bgdColor = colorResource(id = R.color.promo_red))
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        modifier = Modifier
                            .background(colorResource(id = R.color.color_transparent))
                            .size(24.dp),
                        painter = painterResource(id = R.drawable.ic_favorite_black),
                        tint = colorResource(id = R.color.text_grey_composable),
                        contentDescription = "",
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ){
                    PromoTextCell(text = "Акция", bgdColor = colorResource(id = R.color.promo_red))
                    PromoTextCell(text = "Хит", bgdColor = colorResource(id = R.color.hit_green))
                }
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ){
                    PromoTextCell(text = "Волна призов", bgdColor = colorResource(id = R.color.promo_purple))
                    PromoTextCell(text = "новинка", bgdColor = colorResource(id = R.color.new_product_blue))
                }
            }
        }
    }
}

@Composable
fun PromoTextCell(text: String, bgdColor: Color){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(end = 4.dp)
            .height(16.dp)
            .background(
                color = bgdColor,
                shape = RoundedCornerShape(6.dp)
            ),
    ) {
        Text(
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.white),
            text = text,
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp, bottom = 1.dp)
                .background(color = bgdColor),
            fontSize = 9.sp
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Suppress("NonSkippableComposable")
@Composable
fun SetImage(
    image: Any?,
    modifier: Modifier,
    shape: RoundedCornerShape,
    contentScale: ContentScale
){
    Card(
        shape = shape,
        modifier = modifier
    ) {
        if (image == null)
            Image(
                painter = painterResource(id = R.drawable.ic_water_settings),
                contentDescription = null,
                modifier = modifier,
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit
            )
        else
            GlideImage(
                model = image,
                contentDescription = null,
                modifier = modifier.background(Color.White),
                contentScale = contentScale
            )
    }
}

@Composable
fun TextPart(){
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "350 Р",
                modifier = Modifier,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.text_black_composable)
            )
            Text(
                textDecoration = TextDecoration.LineThrough,
                text = "350 Р",
                modifier = Modifier.padding(start = 4.dp),
                fontSize = 10.sp,
                color = colorResource(id = R.color.text_grey_composable)
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "4.7",
                    modifier = Modifier,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.text_black_composable)
                )
            }
        }
        Text(
            text = "какое-то интересное не короткое название какой-то вкусное воды, еcwecewcewcewcewcewcewcewcwecwecwecwecwecwecwecще что-то и еще",
            modifier = Modifier,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketButton(){
    //Button(
    //    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.new_product_blue)),
    //    modifier = Modifier
    //        .fillMaxWidth()
    //        .background(
    //            shape = RoundedCornerShape(12.dp),
    //            color = colorResource(id = R.color.new_product_blue)
    //        )
    //        .height(38.dp),
    //    onClick = {  }
    //) {
    //    Text(
    //        text = "В корзину",
    //        modifier = Modifier,
    //        fontSize = 14.sp,
    //        fontWeight = FontWeight.Bold,
    //        color = colorResource(id = R.color.white)
    //    )
    //}


    var productAmount by remember {
        mutableStateOf("")
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(12.dp),
                color = colorResource(id = R.color.hit_green)
            )
            .fillMaxWidth()
    ) {
        Button(
            onClick = {

            },
            modifier = Modifier
                .fillMaxHeight(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.hit_green)
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp)
        ){
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = colorResource(id = R.color.white),
                modifier = Modifier
                    .size(12.dp)
            )
        }

        TextField(
            modifier = Modifier
                .width(40.dp)
                .background(
                    color = colorResource(id = R.color.blueLight),
                    shape = RoundedCornerShape(0)
                ),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedTextColor = colorResource(id = R.color.white),
                focusedTextColor = colorResource(id = R.color.white),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = colorResource(id = R.color.white),
                containerColor = colorResource(id = R.color.new_product_blue)
            ),
            maxLines = 1,
            textStyle = TextStyle(
                fontSize = 14.sp
            ),
            value = productAmount,
            onValueChange = {
                productAmount = it
            }
        )

        Button(
            onClick = {

            },
            modifier = Modifier
                .fillMaxHeight(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.hit_green)
            ),
            shape = RoundedCornerShape(12.dp)
        ){
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = colorResource(id = R.color.white),
                modifier = Modifier
                    .size(12.dp)
            )
        }

       // Box(
       //     modifier = Modifier
       //         .fillMaxHeight()
       //         .width(40.dp),
       // ){
       //     Icon(
       //         imageVector = Icons.Filled.Add,
       //         contentDescription = null,
       //         tint = colorResource(id = R.color.white),
       //         modifier = Modifier
       //             .size(12.dp)
       //     )
       // }
    }
}

@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (6f * density).dp
    val starSpacing = (0.5f * density).dp

    Row(
        modifier = Modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color(0x20FFFFFF)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
                    .width(starSize)
                    .height(starSize)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}
