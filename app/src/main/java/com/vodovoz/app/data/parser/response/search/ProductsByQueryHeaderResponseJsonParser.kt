package com.vodovoz.app.data.parser.response.search

import android.util.Log
import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.FavoriteProductsHeaderBundleEntity
import com.vodovoz.app.data.parser.common.CategoryJsonParser.parseCategoryEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ProductsByQueryHeaderResponseJsonParser {


    fun ResponseBody.parseProductsByQueryHeaderResponse(): ResponseEntity<CategoryEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.parseCategoryEntity())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
        id = 0,
        name = "",
        shareUrl = getString("detail_page_url"),
        productAmount = getString("tovarvsego"),
        subCategoryEntityList = when(getJSONObject("razdel").isNull("LISTRAZDEL")) {
            true -> listOf()
            false -> getJSONObject("razdel").getJSONArray("LISTRAZDEL").parseSubCategoryEntityList()
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