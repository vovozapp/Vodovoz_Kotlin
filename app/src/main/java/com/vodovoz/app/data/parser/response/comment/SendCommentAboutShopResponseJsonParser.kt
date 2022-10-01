package com.vodovoz.app.data.parser.response.comment

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object SendCommentAboutShopResponseJsonParser {

    fun ResponseBody.parseSendCommentAboutShopResponse(): ResponseEntity<String> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getString("message"))
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

}