package com.vodovoz.app.data.parser.response.favorite

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.FavoriteProductsHeaderBundleEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.SortTypeListJsonParser.parseSortTypeList
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object FavoriteHeaderResponseJsonParser {

    fun ResponseBody.parseFavoriteProductsHeaderBundleResponse(): ResponseEntity<FavoriteProductsHeaderBundleEntity> {
        val responseJson = JSONObject(string())
        return ResponseEntity.Success(
            FavoriteProductsHeaderBundleEntity(
                favoriteCategoryEntity = when(responseJson.has("glavtitle")) {
                    true -> responseJson.parseFavoriteCategoryEntity()
                    false -> null
                },
                bestForYouCategoryDetailEntity = when(responseJson.has("tovary")) {
                    true -> CategoryDetailEntity(
                        id = 0,
                        name = responseJson.getString("title_tovary"),
                        productEntityList = responseJson.getJSONArray("tovary").parseProductEntityList()
                    )
                    false -> null
                },
                availableTitle = when(responseJson.has("nalichie") && !responseJson.isNull("nalichie")) {
                    true -> responseJson.getJSONObject("nalichie").getString("NAMENALICHIE")
                    false -> null
                },
                notAvailableTitle = when(responseJson.has("nalichie") && !responseJson.isNull("nalichie")) {
                    true -> responseJson.getJSONObject("nalichie").getString("NAMENETNALICHIE")
                    false -> null
                },
                title = when(responseJson.has("title")) {
                    true -> responseJson.getString("title")
                    false -> null
                },
               message = when(responseJson.has("message")) {
                    true -> responseJson.getString("message")
                    false -> null
                }
            )
        )
    }

    private fun JSONObject.parseFavoriteCategoryEntity() = CategoryEntity(
        id = 0,
        name = getString("glavtitle"),
        productAmount = getString("tovarvsego"),
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
            add(getJSONObject(index).parseSubCategoryEntityList())
        }
    }

    private fun JSONObject.parseSubCategoryEntityList() = CategoryEntity(
        id = getString("ID").toLong(),
        name = getString("NAME")
    )

}