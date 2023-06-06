package com.vodovoz.app.data.parser.response.search

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.DefaultSearchDataBundleEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object DefaultSearchDataResponseJsonParser {

    fun ResponseBody.parseDefaultSearchDataResponse(): ResponseEntity<DefaultSearchDataBundleEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(DefaultSearchDataBundleEntity(
                popularQueryList = responseJson.getJSONObject("data").getJSONArray("NAME").parsePopularQueryList(),
                popularProductsCategoryDetailEntity = responseJson.getJSONObject("rekomend").parseCategoryDetailEntity()
            ))
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseCategoryDetailEntity() = CategoryDetailEntity(
        id = 0,
        name = safeString("NAME"),
        productEntityList = getJSONArray("REKOMEND").parseProductEntityList()
    )

    private fun JSONArray.parsePopularQueryList(): List<String> = mutableListOf<String>().also { list ->
        for (index in 0 until length()) {
            list.add(getString(index))
        }
    }

}