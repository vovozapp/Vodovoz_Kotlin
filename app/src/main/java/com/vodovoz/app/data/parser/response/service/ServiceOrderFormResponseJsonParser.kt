package com.vodovoz.app.data.parser.response.service

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.ServiceOrderFormFieldEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ServiceOrderFormResponseJsonParser {

    fun ResponseBody.parseServiceOrderFormResponse(): ResponseEntity<List<ServiceOrderFormFieldEntity>> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONArray("data").parseServiceOrderFormEntityList())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseServiceOrderFormEntityList(): List<ServiceOrderFormFieldEntity> =
        mutableListOf<ServiceOrderFormFieldEntity>().also { list ->
            for (index in 0 until length()) list.add(getJSONObject(index).parseServiceOrderFormFieldEntity())
        }

    private fun JSONObject.parseServiceOrderFormFieldEntity() = ServiceOrderFormFieldEntity(
        id = getString("ID"),
        name = getString("NAME"),
        defaultValue = getString("DANNYE"),
        isRequired = when(getString("OBYZ")) {
            "Y" -> true
            else -> false
        }
    )


}