package com.vodovoz.app.data.parser.response

import android.util.Log
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONObject

object LoginResponseJsonParser {

    fun ResponseBody.parseLoginResponse(): ResponseEntity<Long> {
        val responseJson = JSONObject(this.string())
        Log.i(LogSettings.ID_LOG, responseJson.toString())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONObject("data").getLong("user_id"))
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

}