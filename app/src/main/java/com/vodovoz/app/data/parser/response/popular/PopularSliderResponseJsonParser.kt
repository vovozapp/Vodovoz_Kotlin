package com.vodovoz.app.data.parser.response.popular

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.CategoryMainEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.CategoryJsonParser.parseDetailImage
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object PopularSliderResponseJsonParser {

    fun ResponseBody. parsePopularSliderResponse(): ResponseEntity<CategoryMainEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                CategoryMainEntity(
                    name = responseJson.safeString("TITLERAZDEL"),
                    detailPicture = responseJson.parseDetailImage() ?: "",
                    categoryList = responseJson.getJSONObject("data").getJSONArray("LISTRAZDEL")
                        .parseCategoryEntityList()
                )
            )
            else -> ResponseEntity.Error("Ошибка парсинга популярные разделы")
        }
    }

    private fun JSONArray.parseCategoryEntityList(): List<CategoryEntity> =
        mutableListOf<CategoryEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseCategoryEntity())
            }
        }

    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
        id = getLong("IDRAZDEL"),
        name = getString("NAMERAZDEL"),
        detailPicture = parseDetailImage(),
    )

}