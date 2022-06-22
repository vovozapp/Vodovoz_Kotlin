package com.vodovoz.app.data.parser.response.discount

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.CountryBundleEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser.parseCountryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object DiscountHeaderResponseJsonParser {

    fun ResponseBody.parseDiscountHeaderResponse(): ResponseEntity<CategoryEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.parseCategoryEntity())
            else -> ResponseEntity.Error()
        }
    }

    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
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