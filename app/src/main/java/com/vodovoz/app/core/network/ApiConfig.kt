package com.vodovoz.app.core.network

object ApiConfig {
    private const val VODOVOZ_PROTOCOL = "https://"

    private const val VODOVOZ_IP = "m.vodovoz.ru/"
//    private const val VODOVOZ_IP = "szorin.vodovoz.ru/"

    var VODOVOZ_URL = "$VODOVOZ_PROTOCOL$VODOVOZ_IP"

    private const val MAPKIT_PROTOCOL = "https://"
    private const val MAPKIT_IP = "geocode-maps.yandex.ru"

    const val MAPKIT_URL = "$MAPKIT_PROTOCOL$MAPKIT_IP"

    var ABOUT_SHOP_URL = VODOVOZ_URL + "newmobile/informatsiya/omagazine.php"
    var ABOUT_PAY_URL = VODOVOZ_URL + "newmobile/informatsiya/oplata.php"
    var ABOUT_DELIVERY_URL = VODOVOZ_URL + "newmobile/informatsiya/dosytavka.php"
    const val PERSONAL_DATA_URL =
        "https://vodovoz.ru/delivery_files/informaciya/personalnie-dannie.php"

    const val AMOUNT_CONTROLLER_TIMER = 1500L

    const val RUTUBE_URL = "https://rutube.ru/video/"

}