package com.vodovoz.app.feature.profile.waterapp.model

sealed interface WaterAppUiState {

    data object Welcome: WaterAppUiState

    enum class UserData: WaterAppUiState {
        Gender, Height, Weight, WakeUpTime, SleepTime, ActivityLevel,  WaterGoal
    }

    data object Settings: WaterAppUiState

    data object Main: WaterAppUiState

    data object GoalCompleted: WaterAppUiState

}