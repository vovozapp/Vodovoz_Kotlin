package com.vodovoz.app.data.parser.response.category

import com.vodovoz.app.data.model.common.FilterBundleEntity
import com.vodovoz.app.data.model.common.FilterEntity
import com.vodovoz.app.data.model.common.FilterPriceEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.Values
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object AllFiltersByCategoryResponseJsonParser {

    fun ResponseBody.parseAllFiltersByCategoryResponse(): ResponseEntity<FilterBundleEntity> {
        val responseJson = JSONObject(this.string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                FilterBundleEntity(
                    filterPriceEntity = responseJson.getJSONObject("CENAFILTER")
                        .parseFilterPriceEntity(),
                    filterEntityList = responseJson.getJSONArray("data").parseFilterEntityList()
                )
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseFilterPriceEntity() = FilterPriceEntity(
        minPrice = get("MIN").toString().toInt(),
        maxPrice = get("MAX").toString().toInt()
    )

    private fun JSONArray.parseFilterEntityList(): List<FilterEntity> =
        mutableListOf<FilterEntity>().also {
            for (index in 0 until this.length()) {
                it.add(this.optJSONObject(index).parseFilterEntity())
            }
        }

    private fun JSONObject.parseFilterEntity() = FilterEntity(
        code = getString("CODE"),
        name = getString("NAME"),
        type = if (!isNull("TYPE")) {
            getString("TYPE")
        } else {
            null
        },
        values = if (!isNull("ZHACFILTER")) {
            getJSONObject("ZHACFILTER").parseRangeValuesEntity()
        } else {
            null
        }
    )

    private fun JSONObject.parseRangeValuesEntity() = Values(
        min = get("MIN").toString().toFloat(),
        max = get("MAX").toString().toFloat()
    )
}