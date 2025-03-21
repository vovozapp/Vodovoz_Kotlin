package com.vodovoz.app.feature.profile.waterapp.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.composables.user_data.WaterAppGenderStage
import com.vodovoz.app.feature.profile.waterapp.composables.user_data.WaterAppHeightStage
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppUiState

@Composable
fun WaterAppUserDataScreen(
    modifier: Modifier = Modifier,
    userDataStage: WaterAppUiState.UserData,
    userData: WaterAppHelper.WaterAppUserData,
    onGenderSelect: (isMan: Boolean) -> Unit,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        WaterAppUserDataTopBar(
            currentStage = userDataStage,
            onBackClick = onBackClick,
            onCloseClick = onCloseClick
        )
        AnimatedContent(targetState = userDataStage, label = "Animated UserDataStages") { state ->
            when (state) {
                WaterAppUiState.UserData.Gender -> {
                    WaterAppGenderStage(
                        onNextClick = { },
                        onGenderSelect = { isMan -> onGenderSelect(isMan) },
                        isMan = userData.gender == "man"
                    )
                }

                WaterAppUiState.UserData.Height -> {
                    WaterAppHeightStage(onNextClick = { })
                }

                WaterAppUiState.UserData.Weight -> {

                }

                WaterAppUiState.UserData.WakeUpTime -> {

                }

                WaterAppUiState.UserData.SleepTime -> {

                }

                WaterAppUiState.UserData.ActivityLevel -> {

                }

                WaterAppUiState.UserData.WaterGoal -> {

                }
            }
        }
    }
}

@Composable
fun WaterAppUserDataTopBar(
    modifier: Modifier = Modifier,
    currentStage: WaterAppUiState.UserData,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    val stages = (WaterAppUiState.UserData.entries - WaterAppUiState.UserData.WaterGoal)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_left),
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable {
                    onBackClick()
                },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {

            stages.forEachIndexed { index, _ ->
                val stageIndicatorColor by
                animateColorAsState(
                    targetValue = if (index <= currentStage.ordinal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    label = " IndicatorColorAnimation"
                )

                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(stageIndicatorColor),
                )

            }
        }
        Icon(
            painter = painterResource(id = R.drawable.icon_close),
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable {
                    onCloseClick()
                },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}