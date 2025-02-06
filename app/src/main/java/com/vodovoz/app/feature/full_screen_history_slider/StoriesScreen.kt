package com.vodovoz.app.feature.full_screen_history_slider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.feature.full_screen_history_slider.composables.StoriesIndicator

@Composable
fun StoriesScreen(modifier: Modifier = Modifier) {
    HorizontalPager(
        modifier = modifier.fillMaxSize(),
        state = rememberPagerState { 1 }
    ) {
        Box {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = "",
                contentDescription = null
            )
            Column(modifier = Modifier.fillMaxSize()) {
                StoriesIndicator(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(top = 8.dp)
                )
                CloseButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp),
                    onCloseClick = {

                    }
                )
                Spacer(modifier = Modifier.weight(1f))

                VodovozButton(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    text = "",
                    onClick = { },
                    colors = ButtonDefaults.filledTonalButtonColors(

                    )
                )
            }
        }

    }
}

@Composable
private fun CloseButton(modifier: Modifier = Modifier, onCloseClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(32.dp)
            .clickable { onCloseClick() }
            .background(MaterialTheme.colorScheme.surfaceTint),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_close_stories),
            contentDescription = null,
            modifier = Modifier.size(13.dp)
        )
    }
}