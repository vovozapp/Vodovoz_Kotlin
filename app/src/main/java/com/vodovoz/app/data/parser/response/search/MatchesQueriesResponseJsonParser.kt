package com.vodovoz.app.data.parser.response.search

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.QuickQueryBundle
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
                    quickQueryBundle = if (responseJson.has("data") && !responseJson.isNull("data")) {
                        responseJson.getJSONObject("data").parseQueryBundle()
                    } else {
                        null
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
        productEntityList = if(has("TOVARY") && !isNull("TOVARY")){
            getJSONArray("TOVARY").parseProductEntityList()
        } else {
            emptyList()
        }
    )
    private fun JSONObject.parseQueryBundle() = QuickQueryBundle(
        name = safeString("NAME"),
        errorText = safeString("TEXTOSHIBKI"),
        quickQueryList = if(has("SLOVA") && !isNull("SLOVA")) {
            getJSONArray("SLOVA").parseQueryList()
        } else {
            emptyList()
        }
    )
}
