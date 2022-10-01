package com.vodovoz.app.data.parser.response.contacts

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object SendMailResponseJsonParser {

    fun ResponseBody.parseSendMailResponse(): ResponseEntity<String> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getString("data"))
            else -> ResponseEntity.Error(responseJson.getString("data"))
        }
    }

}