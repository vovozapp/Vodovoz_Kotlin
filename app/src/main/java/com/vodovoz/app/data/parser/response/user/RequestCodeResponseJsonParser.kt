package com.vodovoz.app.data.parser.response.user

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object RequestCodeResponseJsonParser {

    fun ResponseBody.parseRequestCodeResponse(): ResponseEntity<Int> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.safeString("time").toInt())
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

}