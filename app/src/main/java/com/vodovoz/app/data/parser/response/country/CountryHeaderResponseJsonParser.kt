package com.vodovoz.app.data.parser.response.country

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object CountryHeaderResponseJsonParser {

    fun ResponseBody.parseCountryHeaderResponse(): ResponseEntity<CategoryEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.parseCategoryEntity())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
        name = getString("glavtitle"),
        shareUrl = getString("detail_page_url_ios"),
        productAmount = getString("tovarvsego"),
        subCategoryEntityList = when(getJSONObject("razdel").isNull("LISTRAZDEL")) {
            true -> listOf()
            false -> getJSONObject("razdel").getJSONArray("LISTRAZDEL").parseSubCategoryEntityList()
        }
    )

    private fun JSONArray.parseSubCategoryEntityList(): List<CategoryEntity> = mutableListOf<CategoryEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseSubCategoryEntityList())
        }
    }

    private fun JSONObject.parseSubCategoryEntityList() = CategoryEntity(
        id = getString("ID").toLong(),
        name = getString("NAME")
    )

}