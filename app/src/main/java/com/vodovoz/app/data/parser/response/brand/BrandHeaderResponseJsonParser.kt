package com.vodovoz.app.data.parser.response.brand

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.SortTypeListJsonParser.parseSortTypeList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object BrandHeaderResponseJsonParser {

    fun ResponseBody.parseBrandHeaderResponse(): ResponseEntity<CategoryEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.parseCategoryEntity())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
        id = getJSONArray("data").getJSONObject(0).getString("ID").toLong(),
        shareUrl = getString("detail_page_url"),
        name = getJSONArray("data").getJSONObject(0).getString("NAME"),
        productAmount = getString("tovarvsego"),
        subCategoryEntityList = when(getJSONObject("razdel").isNull("LISTRAZDEL")) {
            true -> listOf()
            false -> getJSONObject("razdel").getJSONArray("LISTRAZDEL").parseSubCategoryEntityList()
        },
        sortTypeList = when(has("sortirovka")){
            true -> getJSONObject("sortirovka").parseSortTypeList()
            false -> null
        }
    )

    private fun JSONArray.parseSubCategoryEntityList(): List<CategoryEntity> = mutableListOf<CategoryEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseSubCategoryEntity())
        }
    }

    private fun JSONObject.parseSubCategoryEntity() = CategoryEntity(
        id = getString("ID").toLong(),
        name = getString("NAME")
    )

}