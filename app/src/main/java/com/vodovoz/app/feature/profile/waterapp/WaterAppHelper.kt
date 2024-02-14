package com.vodovoz.app.feature.profile.waterapp

import android.app.Application
import android.content.SharedPreferences
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.squareup.moshi.Moshi
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.feature.profile.waterapp.worker.WaterAppWorker
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.fetchCurrentDayInTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterAppHelper @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    moshi: Moshi,
    private val applicationContext: Application,
    private val accountManager: AccountManager,
) {

    companion object {
        const val WATER_APP_USER_DATA = "water app user data"
        const val WATER_APP_NOTIFICATION_DATA = "water app notification data"
        const val WATER_APP_RATE = "water app rate"
    }

    private val adapter = moshi.adapter(WaterAppUserData::class.java)
    private val adapterNotification = moshi.adapter(WaterAppNotificationData::class.java)
    private val adapterRate = moshi.adapter(WaterAppRateData::class.java)

    private val waterAppUserDataListener = MutableStateFlow<WaterAppUserData?>(null)
    fun observeWaterAppUserData() = waterAppUserDataListener.asStateFlow()

    private val waterAppNotificationDataListener = MutableStateFlow<WaterAppNotificationData?>(null)
    fun observeWaterAppNotificationData() = waterAppNotificationDataListener.asStateFlow()

    private val waterAppRateDataListener = MutableStateFlow<WaterAppRateData?>(null)
    fun observeWaterAppRateData() = waterAppRateDataListener.asStateFlow()

    fun fetchWaterAppUserData() {
        if (sharedPrefs.contains(WATER_APP_USER_DATA)) {
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

    fun saveStart(started: Boolean) {
        waterAppNotificationDataListener.value = waterAppNotificationDataListener.value?.copy(
            started = started
        )
    }

    fun saveRate(): Int {
        val rate = calculateRate()
        waterAppRateDataListener.value = waterAppRateDataListener.value?.copy(rate = rate)
        saveWaterAppRateData()
        return rate
    }

    fun fetchWaterAppRateData() {
        val currentDate = fetchCurrentDayInTimeMillis()
        if (sharedPrefs.contains(WATER_APP_RATE)) {
            val json = sharedPrefs.getString(WATER_APP_RATE, "")
            debugLog { "json contains $json" }
            if (!json.isNullOrEmpty()) {
                val data = adapterRate.fromJson(json) ?: return
                debugLog { "data contains $json" }

                if (data.lastSavedDate == 0L) {
                    waterAppRateDataListener.value = data.copy(
                        lastSavedDate = currentDate
                    )
                } else if (data.lastSavedDate != currentDate) {
                    waterAppRateDataListener.value = data.copy(
                        lastSavedDate = currentDate,
                        currentLevel = 0
                    )
                } else {
                    waterAppRateDataListener.value = data
                }
            } else {
                waterAppRateDataListener.value = WaterAppRateData(lastSavedDate = currentDate)
            }
        } else {
            waterAppRateDataListener.value = WaterAppRateData(lastSavedDate = currentDate)
        }
    }

    fun startCalculate() {

        accountManager.reportYandexMetrica("trekervodi_vhod")
    }

    fun tryToChangeWaterLevel(step: Int) {

        accountManager.reportYandexMetrica("trekervodi_chasha")

        val rateState = waterAppRateDataListener.value ?: return

        debugLog { "try to change $step rateState $rateState currentLevel ${rateState.currentLevel} canFill ${rateState.canFill}" }

        if (rateState.currentLevel == rateState.rate) {
            waterAppRateDataListener.value = waterAppRateDataListener.value?.copy(
                canFill = false
            )
            return
        }

        if (step + rateState.currentLevel > rateState.rate) {
            val newStep = rateState.rate - rateState.currentLevel

            waterAppRateDataListener.value = waterAppRateDataListener.value?.copy(
                canFill = true,
                currentLevel = rateState.currentLevel + newStep
            )
        } else {
            waterAppRateDataListener.value = waterAppRateDataListener.value?.copy(
                canFill = true,
                currentLevel = rateState.currentLevel + step
            )
        }
    }

    fun saveWaterAppRateData() {

        val data = waterAppRateDataListener.value

        val json = adapterRate.toJson(data)

        debugLog { "json $json" }

        sharedPrefs
            .edit()
            .putString(WATER_APP_RATE, json)
            .apply()
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
        if (sharedPrefs.contains(WATER_APP_NOTIFICATION_DATA)) {
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

        val data = waterAppNotificationDataListener.value ?: return

        if (data.switch) {
            WorkManager.getInstance(applicationContext).cancelAllWorkByTag("water")
            val work = PeriodicWorkRequest.Builder(
                WaterAppWorker::class.java,
                data.time.toLong(),
                TimeUnit.MINUTES
            )
                .setConstraints(Constraints.NONE)
                .setInitialDelay(data.time.toLong(), TimeUnit.MINUTES)
//                .setInitialDelay(30L, TimeUnit.SECONDS)
                .addTag("water")
                .build()
            WorkManager
                .getInstance(applicationContext)
                .enqueueUniquePeriodicWork("water", ExistingPeriodicWorkPolicy.UPDATE, work)
        } else {
            WorkManager
                .getInstance(applicationContext)
                .cancelAllWorkByTag("water")
        }

        val json = adapterNotification.toJson(data)

        debugLog { "json $json" }

        sharedPrefs
            .edit()
            .putString(WATER_APP_NOTIFICATION_DATA, json)
            .apply()
    }

    fun fetchAppNotificationData(): WaterAppNotificationData {
        if (sharedPrefs.contains(WATER_APP_NOTIFICATION_DATA)) {
            val json = sharedPrefs.getString(WATER_APP_NOTIFICATION_DATA, "")
            debugLog { "json contains $json" }
            if (!json.isNullOrEmpty()) {
                val data = adapterNotification.fromJson(json) ?: return WaterAppNotificationData()
                debugLog { "data contains $json" }
                return data
            } else {
                return WaterAppNotificationData()
            }
        } else {
            return WaterAppNotificationData()
        }
    }

    fun clearData() {
        sharedPrefs.edit()
            .remove(WATER_APP_NOTIFICATION_DATA)
            .remove(WATER_APP_USER_DATA)
            .remove(WATER_APP_RATE)
            .apply()

        fetchWaterAppUserData()
        fetchWaterAppNotificationData()
        fetchWaterAppRateData()
    }

    data class WaterAppNotificationData(
        val firstShow: Boolean = false,
        val switch: Boolean = false,
        val time: String = "60",
        val started: Boolean = false,
    )

    data class WaterAppUserData(
        val gender: String = "man",
        val height: String = "160",
        val weight: String = "50",
        val sleepTime: String = "138",
        val wakeUpTime: String = "42",
        val sport: String = "0.25",
    )

    data class WaterAppRateData(
        val rate: Int = 2300,
        val currentLevel: Int = 0,
        val lastSavedDate: Long = 0,
        val canFill: Boolean = true,
    )
}