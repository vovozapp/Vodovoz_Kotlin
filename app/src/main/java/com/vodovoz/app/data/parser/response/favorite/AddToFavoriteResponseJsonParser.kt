package com.vodovoz.app.data.parser.response.favorite

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONObject
import timber.log.Timber

object AddToFavoriteResponseJsonParser {

    fun ResponseBody.parseAddToFavoriteResponse(): ResponseEntity<String> {
        val responseJson = JSONObject(string())
        Timber.tag(LogSettings.NETWORK_LOG).d(responseJson.toString(2))
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success("Товар добалвен в избранное")
            else -> ResponseEntity.Error("Ошибка!")
        }
    }

}