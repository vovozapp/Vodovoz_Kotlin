package com.vodovoz.app.util

import android.content.Context
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object SplashFileConfig {

    private const val FILE_NAME = "splash.json"
    private const val DAFAULT_LINK = "https://vodovoz.ru/images/zastavka/zastavkamobil.json"

    fun getSplashFile(context: Context) : File {
        return File(context.filesDir, FILE_NAME)
    }

    suspend fun downloadSplashFile(context: Context, link: String = DAFAULT_LINK) {
            withContext(Dispatchers.IO) {
                URL(link).openStream()
            }.use { input ->
                try {
                    val file = getSplashFile(context)
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                } catch (e: Exception) {
                    debugLog { e.message.toString() }
                }
            }
    }
}