package com.vodovoz.app.feature.profile.waterapp

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val adapter = moshi.adapter(WaterAppViewModel.WaterAppUserData::class.java)

    private val waterAppUserDataListener = MutableStateFlow(
        WaterAppViewModel.WaterAppUserData()
    )
    fun observeWaterAppUserData() = waterAppUserDataListener.asStateFlow()

    fun fetchWaterAppUserData() {
        if(sharedPrefs.contains(WATER_APP_USER_DATA)) {
            val json = sharedPrefs.getString(WATER_APP_USER_DATA, "")
            if (!json.isNullOrEmpty()) {
                val data = adapter.fromJson(json) ?: return
                waterAppUserDataListener.value = data
            }
        }
    }

    fun saveWaterAppUserData(data: WaterAppViewModel.WaterAppUserData) {

        val json = adapter.toJson(data)

        debugLog { "json $json" }

        sharedPrefs
            .edit()
            .putString(WATER_APP_USER_DATA, json)
            .apply()
    }
}