package com.vodovoz.app.data.parser.response.user

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object RegisterResponseJsonParser {

    fun ResponseBody.parseRegisterResponse(): ResponseEntity<Long> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getLong("id"))
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

}