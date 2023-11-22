package com.vodovoz.app.data.parser.response.novelties

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.SortTypeListJsonParser.parseSortTypeList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object NoveltiesHeaderResponseJsonParser {

    fun ResponseBody.parseNoveltiesHeaderResponse(): ResponseEntity<CategoryEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.parseCategoryEntity())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
        id = 0,
        name = getString("glavtitle"),
        shareUrl = getString("detail_page_url"),
        productAmount = getString("tovarvsego"),
        subCategoryEntityList = when (getJSONObject("razdel").isNull("LISTRAZDEL")) {
            true -> listOf()
            false -> getJSONObject("razdel").getJSONArray("LISTRAZDEL").parseSubCategoryEntityList()
        },
        sortTypeList = when (has("sortirovka")) {
            false -> null
            true -> getJSONObject("sortirovka").parseSortTypeList()
        }
    )

    private fun JSONArray.parseSubCategoryEntityList(): List<CategoryEntity> =
        mutableListOf<CategoryEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseSubCategoryEntityList())
            }
        }

    private fun JSONObject.parseSubCategoryEntityList() = CategoryEntity(
        id = getString("ID").toLong(),
        name = getString("NAME")
    )

}