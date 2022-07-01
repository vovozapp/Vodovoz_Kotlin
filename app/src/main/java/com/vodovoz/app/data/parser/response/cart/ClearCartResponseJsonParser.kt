package com.vodovoz.app.data.parser.response.cart

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object ClearCartResponseJsonParser {

    fun ResponseBody.parseClearCartResponse(): ResponseEntity<Boolean> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(true)
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

}