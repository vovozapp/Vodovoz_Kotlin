package com.vodovoz.app.core.network

object ApiConfig {
    private const val VODOVOZ_PROTOCOL = "http://"
    private const val VODOVOZ_IP = "m.vodovoz.ru/"

    const val VODOVOZ_URL = "$VODOVOZ_PROTOCOL$VODOVOZ_IP"
    //const val VODOVOZ_URL = "http://szorinvodovoz.tw1.ru/"
    //const val VODOVOZ_URL = "http://mvodovoz.tw1.ru/"

    private const val MAPKIT_PROTOCOL = "https://"
    private const val MAPKIT_IP = "geocode-maps.yandex.ru"

    const val MAPKIT_URL = "$MAPKIT_PROTOCOL$MAPKIT_IP"

    const val ABOUT_SHOP_URL = VODOVOZ_URL + "newmobile/informatsiya/omagazine.php"
    const val ABOUT_PAY_URL = VODOVOZ_URL + "newmobile/informatsiya/oplata.php"
    const val ABOUT_DELIVERY_URL = VODOVOZ_URL + "newmobile/informatsiya/dosytavka.php"

    const val AMOUNT_CONTROLLER_TIMER = 1500L

}