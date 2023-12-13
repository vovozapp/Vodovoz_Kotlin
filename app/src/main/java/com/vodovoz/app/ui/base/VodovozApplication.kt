package com.vodovoz.app.ui.base

import android.app.Application
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.common.notification.NotificationChannels
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.util.Keys
import com.vodovoz.app.util.Keys.YANDEX_METRICA_KEY
import com.yandex.mapkit.MapKitFactory
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import timber.log.Timber

@HiltAndroidApp
class VodovozApplication : Application() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()
        //initYandexMetrica() //todo релиз
        MapKitFactory.setApiKey(Keys.MAPKIT_API_KEY)
        Timber.plant(Timber.DebugTree())
        NotificationChannels.create(this)

//        val file = SplashFileConfig.getSplashFile(this)
//        if (!file.exists()) {
//            debugLog { "splash file is not exist" }
//            coroutineScope.launch(Dispatchers.IO) {
//                downloadSplashFile(this@VodovozApplication)
//            }
//        }
    }

    override fun onTerminate() {
        coroutineScope.cancel()
        super.onTerminate()
    }

    private fun initYandexMetrica() {
        val config: YandexMetricaConfig =
            YandexMetricaConfig.newConfigBuilder(YANDEX_METRICA_KEY)
                .withNativeCrashReporting(false)
                .withLocationTracking(false)
                .withAppVersion(BuildConfig.VERSION_NAME)
                .withUserProfileID(ApiConfig.VODOVOZ_URL)
                .withLogs()
                .build()
        YandexMetrica.activate(this, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}