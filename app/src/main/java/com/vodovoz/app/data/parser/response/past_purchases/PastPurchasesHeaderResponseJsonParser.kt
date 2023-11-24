package com.vodovoz.app.data.parser.response.past_purchases

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.PastPurchasesHeaderBundleEntity
import com.vodovoz.app.data.parser.common.SortTypeListJsonParser.parseSortTypeList
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.util.LogSettings
import com.vodovoz.app.util.extensions.debugLog
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object PastPurchasesHeaderResponseJsonParser {

    fun ResponseBody.parsePastPurchasesHeaderResponse(): ResponseEntity<PastPurchasesHeaderBundleEntity> {
        val responseJson = JSONObject(string())
        debugLog { LogSettings.RESPONSE_BODY_LOG + " ${responseJson.toString(2)}" }
        return ResponseEntity.Success(
            PastPurchasesHeaderBundleEntity(
                favoriteCategoryEntity = when (responseJson.has("glavtitle")) {
                    true -> responseJson.parseFavoriteCategoryEntity()
                    false -> null
                },
                availableTitle = when (responseJson.has("nalichie") && !responseJson.isNull("nalichie")) {
                    true -> responseJson.getJSONObject("nalichie").getString("NAMENALICHIE")
                    false -> null
                },
                notAvailableTitle = when (responseJson.has("nalichie") && !responseJson.isNull("nalichie")) {
                    true -> responseJson.getJSONObject("nalichie").getString("NAMENETNALICHIE")
                    false -> null
                },
                emptyTitle = responseJson.safeString("title"),
                emptySubtitle = responseJson.safeString("message")
            )
        )
    }

    private fun JSONObject.parseFavoriteCategoryEntity() = CategoryEntity(
        id = 0,
        shareUrl = safeString("detail_page_url"),
        name = getString("glavtitle"),
        productAmount = when (isNull("tovarvsego")) {
            true -> "Нет товаров"
            false -> safeString("tovarvsego")
        },
        subCategoryEntityList = when (has("razdel")) {
            true -> when (getJSONObject("razdel").isNull("LISTRAZDEL")) {
                true -> listOf()
                false -> getJSONObject("razdel").getJSONArray("LISTRAZDEL")
                    .parseSubCategoryEntityList()
            }
            false -> listOf()
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