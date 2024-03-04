package com.vodovoz.app.data.parser.response.search

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.QuickSearchDataBundleEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object MatchesQueriesResponseJsonParser {

    fun ResponseBody.parseMatchesQueriesResponse(): ResponseEntity<QuickSearchDataBundleEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                QuickSearchDataBundleEntity(
                    quickQueryList = if (responseJson.has("data") && !responseJson.isNull("data")) {
                        responseJson.getJSONArray("data").parseQueryList()
                    } else {
                        listOf()
                    },
                    quickProductsCategoryDetailEntity = if(responseJson.has("tovary") && !responseJson.isNull("tovary")) {
                        responseJson.getJSONObject("tovary").parseCategoryDetailEntity()
                    } else {
                        null
                    }
                )
            )

            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseQueryList(): List<String> = mutableListOf<String>().also { list ->
        for (index in 0 until length()) {
            list.add(getString(index))
        }
    }

    private fun JSONObject.parseCategoryDetailEntity() = CategoryDetailEntity(
        id = 0,
        name = safeString("NAME"),
        productEntityList = getJSONArray("TOVARY").parseProductEntityList()
    )
}