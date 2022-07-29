package com.vodovoz.app.data.parser.response.past_purchases

import android.util.Log
import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.FavoriteProductsHeaderBundleEntity
import com.vodovoz.app.data.model.features.PastPurchasesHeaderBundleEntity
import com.vodovoz.app.data.parser.common.CategoryJsonParser.parseCategoryEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object PastPurchasesHeaderResponseJsonParser {

    fun ResponseBody.parsePastPurchasesHeaderResponse(): ResponseEntity<PastPurchasesHeaderBundleEntity> {
        val responseJson = JSONObject(string())
        return ResponseEntity.Success(
            PastPurchasesHeaderBundleEntity(
                favoriteCategoryEntity = when(responseJson.has("glavtitle")) {
                    true -> responseJson.parseFavoriteCategoryEntity()
                    false -> null
                },
                availableTitle = when(responseJson.has("nalichie") && !responseJson.isNull("nalichie")) {
                    true -> responseJson.getJSONObject("nalichie").getString("NAMENALICHIE")
                    false -> null
                },
                notAvailableTitle = when(responseJson.has("nalichie") && !responseJson.isNull("nalichie")) {
                    true -> responseJson.getJSONObject("nalichie").getString("NAMENETNALICHIE")
                    false -> null
                }
            )
        )
    }

    private fun JSONObject.parseFavoriteCategoryEntity() = CategoryEntity(
        id = 0,
        shareUrl = getString("detail_page_url"),
        name = getString("glavtitle"),
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