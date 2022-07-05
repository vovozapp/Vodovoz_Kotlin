package com.vodovoz.app.data.parser.response.discount

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object DiscountSliderResponseParser {

    fun ResponseBody.parseDiscountSliderResponse(): ResponseEntity<List<CategoryDetailEntity>> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.parseCategoryDetailEntityList())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseCategoryDetailEntityList(): List<CategoryDetailEntity> = mutableListOf<CategoryDetailEntity>().apply {
        add(parseCategoryDetailEntity())
    }

    private fun JSONObject.parseCategoryDetailEntity() = CategoryDetailEntity(
        id = 0,
        name = getString("glavtitle"),
        productEntityList = getJSONArray("data").parseProductEntityList(),
    )

}