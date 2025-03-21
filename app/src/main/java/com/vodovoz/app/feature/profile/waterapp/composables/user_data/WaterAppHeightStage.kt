package com.vodovoz.app.feature.profile.waterapp.composables.user_data

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.feature.profile.waterapp.composables.HorizontalWheelPicker
import com.vodovoz.app.feature.profile.waterapp.composables.WaterAppButton

@Composable
fun WaterAppHeightStage(
    modifier: Modifier = Modifier,
    onNextClick: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.you_height),
            modifier = Modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .heightIn(300.dp)
                .fillMaxWidth(),

        ) {

            Image(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(82.dp),
                painter = painterResource(id = R.drawable.ic_wheel_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceVariant)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(22.dp)
                    .offset(x = 0.dp, y = 17.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )

            HorizontalWheelPicker(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                totalItems = 100,
                initialSelectedItem = 20,
                onItemSelected = { }
            )

        }




        Spacer(modifier = Modifier.weight(1f))

        WaterAppButton(modifier = Modifier.padding(vertical = 20.dp)) { onNextClick() }

    }
}

@Composable
fun SquareWithNotchAndBall() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val width = size.width
        val height = size.height

        val notchWidth = 44.dp.toPx()
        val notchDepth = 44.dp.toPx()

        val ballMargin = 8.dp.toPx()

        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, 0f)
            lineTo(width, 0f)
            lineTo(width, height)
            lineTo((width + notchWidth) / 2, height)
            lineTo(width / 2, height - notchDepth)
            lineTo((width - notchWidth) / 2, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = path,
            color = Color.Gray
        )

        val ballRadius = notchWidth / 3

        val ballCenter = Offset(
            x = width / 2,
            y = height - notchDepth / 2 + ballMargin
        )

        drawCircle(
            color = Color.Red,
            radius = ballRadius,
            center = ballCenter
        )
    }
}