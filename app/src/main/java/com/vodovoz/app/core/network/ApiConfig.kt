package com.vodovoz.app.core.network

object ApiConfig {
    private const val VODOVOZ_PROTOCOL = "http://"
    private const val VODOVOZ_IP = "m.vodovoz.ru/"

    const val VODOVOZ_URL = "$VODOVOZ_PROTOCOL$VODOVOZ_IP"

    private const val MAPKIT_PROTOCOL = "https://"
    private const val MAPKIT_IP = "geocode-maps.yandex.ru"

    const val MAPKIT_URL = "$MAPKIT_PROTOCOL$MAPKIT_IP"

    const val ABOUT_SHOP_URL = "http://m.vodovoz.ru/newmobile/informatsiya/omagazine.php"
    const val ABOUT_PAY_URL = "http://m.vodovoz.ru/newmobile/informatsiya/oplata.php"
    const val ABOUT_DELIVERY_URL = "http://m.vodovoz.ru/newmobile/informatsiya/dosytavka.php"

    const val AMOUNT_CONTROLLER_TIMER = 1500L

}