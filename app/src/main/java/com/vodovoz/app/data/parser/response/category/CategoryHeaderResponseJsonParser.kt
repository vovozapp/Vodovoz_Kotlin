package com.vodovoz.app.data.parser.response.category

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.FilterValueEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object CategoryHeaderResponseJsonParser {

    fun ResponseBody.parseCategoryHeaderResponse(): ResponseEntity<CategoryEntity> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> {
                val primaryFilterName = when (responseJson.has("brand")) {
                    true -> responseJson.getJSONObject("brand").getString("NAME")
                    else -> null
                }

                val primaryFilterValueList = if (responseJson.has("brand")) {
                    when (responseJson.getJSONObject("brand").isNull("POPYLBRAND")) {
                        false -> {
                            parseBrandFilterValueEntityList(
                                responseJson.getJSONObject("brand").getJSONArray("POPYLBRAND"))
                        }
                        true -> listOf()
                    }
                } else listOf()

                return ResponseEntity.Success(
                    CategoryEntity(
                        id = 0,
                        productAmount = responseJson.getString("tovarvsego"),
                        name = responseJson.getString("titlerazdel"),
                        primaryFilterName = primaryFilterName,
                        primaryFilterValueList = primaryFilterValueList
                    )
                )
            }
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun parseBrandFilterValueEntityList(
        brandCategoryJSONArray: JSONArray
    ): List<FilterValueEntity> = mutableListOf<FilterValueEntity>().apply {
        for (index in 0 until brandCategoryJSONArray.length()) {
            add(parseBrandFilterValueEntity(brandCategoryJSONArray.getJSONObject(index)))
        }
    }

    private fun parseBrandFilterValueEntity(
        brandCategoryJSON: JSONObject
    ) = FilterValueEntity(
        id = brandCategoryJSON.getString("ID"),
        value = brandCategoryJSON.getString("NAME")
    )

}