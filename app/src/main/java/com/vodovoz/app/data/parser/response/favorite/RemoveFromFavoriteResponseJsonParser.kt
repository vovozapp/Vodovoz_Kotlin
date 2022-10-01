package com.vodovoz.app.data.parser.response.favorite

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object RemoveFromFavoriteResponseJsonParser {

    fun ResponseBody.parseRemoveFromFavoriteResponse(): ResponseEntity<String> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success("Товар удален из избранного")
            else -> ResponseEntity.Error("Ошибка!")
        }
    }

}