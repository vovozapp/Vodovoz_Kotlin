package com.vodovoz.app.data.parser.response.order

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object CancelOrderResponseJsonParser {

    fun ResponseBody.parseCancelOrderResponse(): ResponseEntity<String> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getString("message"))
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

}