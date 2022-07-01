package com.vodovoz.app.data.parser.response.popular

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object PopularSliderResponseJsonParser {

    fun ResponseBody.parsePopularSliderResponse(): ResponseEntity<List<CategoryEntity>> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONObject("data").getJSONArray("LISTRAZDEL").parseCategoryEntityList()
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseCategoryEntityList(): List<CategoryEntity> = mutableListOf<CategoryEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseCategoryEntity())
        }
    }

    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
        id = getLong("IDRAZDEL"),
        name = getString("NAMERAZDEL")
    )

}