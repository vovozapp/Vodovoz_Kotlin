package com.vodovoz.app.ui.base

import android.app.Application
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.common.notification.NotificationChannels
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.util.Keys
import com.vodovoz.app.util.Keys.YANDEX_METRICA_KEY
import com.vodovoz.app.util.SplashFileConfig
import com.vodovoz.app.util.extensions.debugLog
import com.yandex.mapkit.MapKitFactory
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.FileOutputStream
import java.net.URL

@HiltAndroidApp
class VodovozApplication : Application() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()
        //initYandexMetrica() //todo релиз
        MapKitFactory.setApiKey(Keys.MAPKIT_API_KEY)
        Timber.plant(Timber.DebugTree())
        NotificationChannels.create(this)

        val file = SplashFileConfig.getSplashFile(this)
        if (!file.exists()) {
            debugLog { "file is not exist" }
            coroutineScope.launch(Dispatchers.IO) {
                download("https://vodovoz.ru/images/zastavka/zastavkamobil.json")
            }
        }
    }

    override fun onTerminate() {
        coroutineScope.cancel()
        super.onTerminate()
    }

    private suspend fun download(link: String) {
        withContext(Dispatchers.IO) {
            URL(link).openStream()
        }.use { input ->
            val file = SplashFileConfig.getSplashFile(this)
            if (!file.exists()) {
                file.createNewFile()
            }
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
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