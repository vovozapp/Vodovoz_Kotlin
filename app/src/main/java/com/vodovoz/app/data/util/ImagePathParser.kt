package com.vodovoz.app.data.util

import com.vodovoz.app.data.config.ApiConfig

object ImagePathParser {

    fun String.parseImagePath() = StringBuilder()
        .append(ApiConfig.VODOVOZ_URL)
        .append(this.replace("\"", ""))
        .toString()

}