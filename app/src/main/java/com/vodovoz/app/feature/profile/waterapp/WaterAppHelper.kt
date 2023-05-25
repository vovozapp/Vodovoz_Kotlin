package com.vodovoz.app.feature.profile.waterapp

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterAppHelper @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val moshi: Moshi
) {

    companion object {
        const val WATER_APP_USER_DATA = "water app user data"
    }

    fun saveWaterAppUserData(data: WaterAppViewModel.WaterAppUserData) {

    }
}