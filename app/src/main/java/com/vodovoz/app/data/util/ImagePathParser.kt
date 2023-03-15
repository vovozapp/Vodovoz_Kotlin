package com.vodovoz.app.data.util

import com.vodovoz.app.core.network.ApiConfig

object ImagePathParser {

    fun String.parseImagePath() = StringBuilder()
        .append(ApiConfig.VODOVOZ_URL)
        .append(this.replace("\"", ""))
        .toString()

}