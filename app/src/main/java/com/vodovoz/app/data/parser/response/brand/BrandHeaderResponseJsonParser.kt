package com.vodovoz.app.data.parser.response.brand

import android.util.Log
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.CountryBundleEntity
import com.vodovoz.app.data.parser.common.CategoryJsonParser.parseCategoryEntityList
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser.parseCountryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object BrandHeaderResponseJsonParser {

    fun ResponseBody.parseBrandHeaderResponse(): ResponseEntity<CategoryEntity> {
        val responseJson = JSONObject(string())
        Log.i(LogSettings.REQ_RES_LOG, responseJson.toString())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.parseCategoryEntity())
            else -> ResponseEntity.Error()
        }
    }

    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
        id = getJSONArray("data").getJSONObject(0).getString("ID").toLong(),
        name = getJSONArray("data").getJSONObject(0).getString("NAME"),
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