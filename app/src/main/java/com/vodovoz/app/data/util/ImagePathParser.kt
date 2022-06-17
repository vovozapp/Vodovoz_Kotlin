package com.vodovoz.app.data.util

import com.vodovoz.app.data.config.ApiConfig

object ImagePathParser {

    fun String.parseImagePath() = StringBuilder()
        .append(ApiConfig.URL)
        .append(this.replace("\"", ""))
        .toString()

}