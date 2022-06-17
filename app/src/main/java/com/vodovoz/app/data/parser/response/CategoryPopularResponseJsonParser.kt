package com.vodovoz.app.data.parser.response

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

class CategoryPopularResponseJsonParser {

    fun parseResponse(
        responseBody: ResponseBody
    ): ResponseEntity<List<CategoryEntity>> {
        val responseJson = JSONObject(responseBody.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                parseCategoryEntityList(responseJson.getJSONObject("data").getJSONArray("LISTRAZDEL")))
            else -> ResponseEntity.Error()
        }
    }

    private fun parseCategoryEntityList(
        categoryJSONArray: JSONArray
    ): List<CategoryEntity> = mutableListOf<CategoryEntity>().apply {
        for (index in 0 until categoryJSONArray.length()) {
            add(parseCategoryEntity(categoryJSONArray.getJSONObject(index)))
        }
    }

    private fun parseCategoryEntity(
        categoryJson: JSONObject
    ) = CategoryEntity(
        id = categoryJson.getLong("IDRAZDEL"),
        name = categoryJson.getString("NAMERAZDEL")
    )
}