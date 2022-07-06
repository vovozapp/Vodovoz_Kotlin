package com.vodovoz.app.data.config

object ApiConfig {
    private const val VODOVOZ_PROTOCOL = "http://"
    private const val VODOVOZ_IP = "m.vodovoz.ru/"

    const val VODOVOZ_URL = "$VODOVOZ_PROTOCOL$VODOVOZ_IP"

    private const val MAPKIT_PROTOCOL = "https://"
    private const val MAPKIT_IP = "geocode-maps.yandex.ru"

    const val MAPKIT_URL = "$MAPKIT_PROTOCOL$MAPKIT_IP"
}