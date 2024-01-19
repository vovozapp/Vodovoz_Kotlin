package com.vodovoz.app.data.parser.response.search

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SearchQueryHeaderResponse
import com.vodovoz.app.data.parser.common.SortTypeListJsonParser.parseSortTypeList
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.parser.response.search.ProductsByQueryHeaderResponseJsonParser.parseCategoryEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ProductsByQueryHeaderResponseJsonParser {


    fun ResponseBody.parseProductsByQueryHeaderResponse(): ResponseEntity<SearchQueryHeaderResponse> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.parseSearchQueryHeaderResponse()
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseSearchQueryHeaderResponse() = if(!isNull("perexod")) {
        SearchQueryHeaderResponse(
            deepLink = safeString("perexod"),
            id = safeString("id")
        )
    } else {
        SearchQueryHeaderResponse(
            category = parseCategoryEntity()
        )
    }

    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
        id = 0,
        name = "",
        shareUrl = safeString("detail_page_url"),
        productAmount = safeString("tovarvsego"),
        subCategoryEntityList = when(getJSONObject("razdel").isNull("LISTRAZDEL")) {
            true -> listOf()
            false -> getJSONObject("razdel").getJSONArray("LISTRAZDEL").parseSubCategoryEntityList()
        },
        sortTypeList = when (has("sortirovka")) {
            false -> null
            true -> getJSONObject("sortirovka").parseSortTypeList()
        }
    )

    private fun JSONArray.parseSubCategoryEntityList(): List<CategoryEntity> = mutableListOf<CategoryEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseSubCategoryEntity())
        }
    }

    private fun JSONObject.parseSubCategoryEntity() = CategoryEntity(
        id = safeString("ID").toLong(),
        name = safeString("NAME")
    )

}