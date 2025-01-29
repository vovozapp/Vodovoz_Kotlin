package com.vodovoz.app.composable.common_views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagingIndicator(pagerState: PagerState){
    val scrollState = rememberLazyListState(pagerState.currentPage)
    val scope = rememberCoroutineScope()
    SideEffect{
        scope.launch {
            if (pagerState.currentPage >= 3)
                scrollState.animateScrollToItem(pagerState.currentPage - 2)
        }
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(4.dp)
    ){
        LazyRow (
            state = scrollState,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(58.dp)
                .border(width = 1.dp, color = colorResource(id = R.color.border_blue))
                .background(color = colorResource(id = R.color.white))
        ){
            items(pagerState.pageCount) {
                IndicatorDot(selected = it == pagerState.currentPage)
            }
        }
    }
}

@Composable
fun IndicatorDot(
    selected: Boolean
){
    Surface (
        modifier = Modifier
            .padding(2.dp)
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    if (selected) colorResource(id = R.color.blueLight) else colorResource(
                        id = R.color.white
                    ), CircleShape
                )
                .alpha(if (selected) 1f else 0.7f)
                .size(if (!selected) 7.dp else 9.dp)
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.blueDark),
                    shape = CircleShape
                )
        ) {

        }
    }
}