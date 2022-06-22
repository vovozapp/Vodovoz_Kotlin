package com.vodovoz.app.data.parser.response.category

import com.vodovoz.app.data.model.common.FilterValueEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ConcreteFilterResponseJsonParser {

    fun ResponseBody.parseConcreteFilterResponse(): ResponseEntity<List<FilterValueEntity>> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").parseFilterValueEntityList())
            else -> ResponseEntity.Error()
        }
    }

    private fun JSONArray.parseFilterValueEntityList():List<FilterValueEntity> =
        mutableListOf<FilterValueEntity>().also {
            for (index in 0 until this.length()) {
                it.add(this.getJSONObject(index).parseFilterValueEntity())
            }
        }

    private fun JSONObject.parseFilterValueEntity() = FilterValueEntity(
        id = getString("ID"),
        value = getString("VALUE")
    )

}