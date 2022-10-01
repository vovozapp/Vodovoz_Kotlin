package com.vodovoz.app.data.parser.response.pre_order

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object PreOrderProductResponseJsonParser {

    fun ResponseBody.parsePreOrderProductResponse(): ResponseEntity<String> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.safeString("message"))
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

}