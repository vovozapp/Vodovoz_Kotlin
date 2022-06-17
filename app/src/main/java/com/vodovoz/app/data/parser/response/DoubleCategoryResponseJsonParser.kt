package com.vodovoz.app.data.parser.response

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

class DoubleCategoryResponseJsonParser {

    fun parseResponse(
        responseBody: ResponseBody
    ): ResponseEntity<List<List<CategoryDetailEntity>>> {
        val responseJson = JSONObject(responseBody.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                mutableListOf<List<CategoryDetailEntity>>().apply {
                    add(parseCategoryDetailEntityList(
                        responseJson.getJSONObject("RAZDEL_VERH").getJSONArray("RAZDELY_VERH")))
                    add(parseCategoryDetailEntityList(
                        responseJson.getJSONObject("RAZDEL_NIZ").getJSONArray("RAZDELY_NIZ")))
                }
            )
            else -> ResponseEntity.Error()
        }
    }

    private fun parseCategoryDetailEntityList(
        categoryJSONArray: JSONArray
    ): List<CategoryDetailEntity> = mutableListOf<CategoryDetailEntity>().apply {
        for (index in 0 until categoryJSONArray.length()) {
            add(parseCategoryDetailEntity(categoryJSONArray.getJSONObject(index)))
        }
    }

    private fun parseCategoryDetailEntity(
        categoryJson: JSONObject
    ): CategoryDetailEntity {
        val productEntityList = categoryJson.getJSONArray("data").parseProductEntityList()
        return CategoryDetailEntity(
            id = categoryJson.getLong("ID"),
            name = categoryJson.getString("NAME"),
            productAmount = productEntityList.size,
            productEntityList = productEntityList
        )
    }

}