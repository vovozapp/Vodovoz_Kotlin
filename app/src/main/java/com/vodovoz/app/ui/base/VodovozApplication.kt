package com.vodovoz.app.ui.base

import android.app.Application
import com.vodovoz.app.common.notification.NotificationChannels
import com.vodovoz.app.util.Keys
import com.vodovoz.app.util.Keys.YANDEX_METRICA_KEY
import com.yandex.mapkit.MapKitFactory
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class VodovozApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //initYandexMetrica() //todo релиз
        MapKitFactory.setApiKey(Keys.MAPKIT_API_KEY)
        Timber.plant(Timber.DebugTree())
        NotificationChannels.create(this)
    }

    private fun initYandexMetrica() {
        val config: YandexMetricaConfig =
            YandexMetricaConfig.newConfigBuilder(YANDEX_METRICA_KEY).build()
        YandexMetrica.activate(this, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}