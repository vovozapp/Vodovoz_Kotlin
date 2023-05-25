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

    private val adapter = moshi.adapter(WaterAppUserData::class.java)

    private val waterAppUserDataListener = MutableStateFlow<WaterAppUserData?>(
        null
    )
    fun observeWaterAppUserData() = waterAppUserDataListener.asStateFlow()

    fun fetchWaterAppUserData() {
        if(sharedPrefs.contains(WATER_APP_USER_DATA)) {
            val json = sharedPrefs.getString(WATER_APP_USER_DATA, "")
            debugLog { "json contains $json" }
            if (!json.isNullOrEmpty()) {
                val data = adapter.fromJson(json) ?: return
                debugLog { "data contains $json" }
                waterAppUserDataListener.value = data
            } else {
                waterAppUserDataListener.value = WaterAppUserData()
            }
        } else {
            waterAppUserDataListener.value = WaterAppUserData()
        }
    }

    fun saveWaterAppUserData() {

        val data = waterAppUserDataListener.value

        val json = adapter.toJson(data)

        debugLog { "json $json" }

        sharedPrefs
            .edit()
            .putString(WATER_APP_USER_DATA, json)
            .apply()
    }

    fun saveGender(gender: String) {
        waterAppUserDataListener.value = waterAppUserDataListener.value?.copy(
            gender = gender
        )
    }

    fun saveHeight(height: String) {
        waterAppUserDataListener.value = waterAppUserDataListener.value?.copy(
            height = height
        )
    }

    fun saveWeight(weight: String) {
        waterAppUserDataListener.value = waterAppUserDataListener.value?.copy(
            weight = weight
        )
    }

    fun saveSleepTime(sleepTime: String) {
        waterAppUserDataListener.value = waterAppUserDataListener.value?.copy(
            sleepTime = sleepTime
        )
    }

    fun saveWakeUpTime(wakeUpTime: String) {
        waterAppUserDataListener.value = waterAppUserDataListener.value?.copy(
            wakeUpTime = wakeUpTime
        )
    }

    fun saveSport(sport: String) {
        waterAppUserDataListener.value = waterAppUserDataListener.value?.copy(
            sport = sport
        )
    }

    data class WaterAppUserData(
        val gender: String = "man",
        val height: String = "160",
        val weight: String = "50",
        val sleepTime: String = "138",
        val wakeUpTime: String = "",
        val sport: String = ""
    )
}