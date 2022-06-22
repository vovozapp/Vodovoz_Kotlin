package com.vodovoz.app.data.parser.response.doubleSlider

import android.util.Log
import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object DoubleSliderResponseJsonParser {

    fun ResponseBody.parseBottomSliderResponse(): ResponseEntity<List<CategoryDetailEntity>> {
        val responseJson = JSONObject(string())
        Log.i(LogSettings.ID_LOG, responseJson.toString())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONObject("RAZDEL_NIZ")
                    .getJSONArray("RAZDELY_NIZ").parseCategoryDetailEntityList()
            )
            else -> ResponseEntity.Error()
        }
    }

    fun ResponseBody.parseTopSliderResponse(): ResponseEntity<List<CategoryDetailEntity>> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONObject("RAZDEL_VERH")
                    .getJSONArray("RAZDELY_VERH").parseCategoryDetailEntityList().apply {
                        Log.i(LogSettings.REQ_RES_LOG, "SIZE $size")
                    }
            )
            else -> ResponseEntity.Error()
        }
    }

    private fun JSONArray.parseCategoryDetailEntityList(): List<CategoryDetailEntity> = mutableListOf<CategoryDetailEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseCategoryDetailEntity())
        }
    }

    private fun JSONObject.parseCategoryDetailEntity(): CategoryDetailEntity {
        val productEntityList = getJSONArray("data").parseProductEntityList()
        return CategoryDetailEntity(
            id = getLong("ID"),
            name = getString("NAME"),
            productAmount = productEntityList.size,
            productEntityList = productEntityList
        )
    }

}