package com.vodovoz.app.data.parser.response.favorite

import android.util.Log
import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object AddToFavoriteResponseJsonParser {

    fun ResponseBody.parseAddToFavoriteResponse(): ResponseEntity<String> {
        val responseJson = JSONObject(string())
        Log.i(LogSettings.REQ_RES_LOG, responseJson.toString())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success("Товар добалвен в избранное")
            else -> ResponseEntity.Error("Ошибка!")
        }
    }

}