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
    private val moshi: Moshi,
) {

    companion object {
        const val WATER_APP_USER_DATA = "water app user data"
        const val WATER_APP_NOTIFICATION_DATA = "water app notification data"
        const val WATER_APP_RATE = "water app rate"
    }

    private val adapter = moshi.adapter(WaterAppUserData::class.java)
    private val adapterNotification = moshi.adapter(WaterAppNotificationData::class.java)

    private val waterAppUserDataListener = MutableStateFlow<WaterAppUserData?>(
        null
    )
    fun observeWaterAppUserData() = waterAppUserDataListener.asStateFlow()

    private val waterAppNotificationDataListener = MutableStateFlow<WaterAppNotificationData?>(
        null
    )
    fun observeWaterAppNotificationData() = waterAppNotificationDataListener.asStateFlow()

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

    fun saveRate() : Int {
        val rate = calculateRate()
        debugLog { "save rate $rate" }
        sharedPrefs
            .edit()
            .putInt(WATER_APP_RATE, rate)
            .apply()
        return rate
    }

    fun fetchRate(): Int {
        return if (!sharedPrefs.contains(WATER_APP_RATE)) {
            saveRate()
        } else {
            sharedPrefs.getInt(WATER_APP_RATE, 2300)
        }
    }

    private fun calculateRate(): Int {
        val weight = waterAppUserDataListener.value?.weight?.toDouble() ?: 50.0
        val sport = waterAppUserDataListener.value?.sport?.toDouble() ?: 0.25
        debugLog { "calculate rate weight $weight sport $sport" }
        return ((1.5 + (weight - 20) * 0.02 + sport) * 1000).toInt()
    }

    fun saveNotificationFirstShow() {
        waterAppNotificationDataListener.value = waterAppNotificationDataListener.value?.copy(
            firstShow = true
        )
    }

    fun saveNotificationSwitch(switch: Boolean) {
        waterAppNotificationDataListener.value = waterAppNotificationDataListener.value?.copy(
            switch = switch
        )
    }

    fun saveNotificationTime(time: String) {
        waterAppNotificationDataListener.value = waterAppNotificationDataListener.value?.copy(
            time = time
        )
    }

    fun fetchWaterAppNotificationData() {
        if(sharedPrefs.contains(WATER_APP_NOTIFICATION_DATA)) {
            val json = sharedPrefs.getString(WATER_APP_NOTIFICATION_DATA, "")
            debugLog { "json contains $json" }
            if (!json.isNullOrEmpty()) {
                val data = adapterNotification.fromJson(json) ?: return
                debugLog { "data contains $json" }
                waterAppNotificationDataListener.value = data
            } else {
                waterAppNotificationDataListener.value = WaterAppNotificationData()
            }
        } else {
            waterAppNotificationDataListener.value = WaterAppNotificationData()
        }
    }

    fun saveWaterAppNotificationData() {

        val data = waterAppNotificationDataListener.value

        val json = adapterNotification.toJson(data)

        debugLog { "json $json" }

        sharedPrefs
            .edit()
            .putString(WATER_APP_NOTIFICATION_DATA, json)
            .apply()
    }

    data class WaterAppNotificationData(
        val firstShow: Boolean = false,
        val switch: Boolean = true,
        val time: String = "60"
    )

    data class WaterAppUserData(
        val gender: String = "man",
        val height: String = "160",
        val weight: String = "50",
        val sleepTime: String = "138",
        val wakeUpTime: String = "42",
        val sport: String = "0.25",
    )
}