package com.vodovoz.app.data.parser.response.cart

import com.vodovoz.app.data.model.common.BottleEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object BottlesResponseJsonParser {

    fun ResponseBody.parseBottlesResponse(): ResponseEntity<List<BottleEntity>> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").parseBottleEntityList()
            )
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

    private fun JSONArray.parseBottleEntityList(): List<BottleEntity> = mutableListOf<BottleEntity>().also { list ->
        for (index in 0 until length()) list.add(getJSONObject(index).parseBottleEntity())
    }
    private fun JSONObject.parseBottleEntity() = BottleEntity(
        id = safeString("ID").toLong(),
        name = safeString("NAME")
    )
}