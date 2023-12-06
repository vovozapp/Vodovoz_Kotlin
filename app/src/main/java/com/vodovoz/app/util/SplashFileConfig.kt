package com.vodovoz.app.util

import android.content.Context
import java.io.File

object SplashFileConfig {

    private const val FILE_NAME = "splash.json"

    fun getSplashFile(context: Context) : File {
        return File(context.filesDir, FILE_NAME)
    }
}