package com.vodovoz.app.data.parser.response.service

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.ServiceEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object ServiceByIdResponseJsonParser {

    fun ResponseBody.parseServiceByIdResponse(
        type: String?
    ): ResponseEntity<ServiceEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONObject("data").parseServiceEntity(type))
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseServiceEntity( type: String?) = ServiceEntity(
        name = safeString("TITLE"),
        detail = safeString("NAME"),
        type = type ?: ""
    )

}