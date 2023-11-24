package com.vodovoz.app.data.parser.response.user

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import com.vodovoz.app.util.extensions.debugLog
import okhttp3.ResponseBody
import org.json.JSONObject

object AuthByPhoneJsonParser {

    fun ResponseBody.parseAuthByPhoneResponse(): ResponseEntity<Long> {
        val responseJson = JSONObject(this.string())
        debugLog { LogSettings.DEVELOP_LOG + " ${responseJson.toString(2)}" }
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONObject("data").getLong("user_id")
            )
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

}