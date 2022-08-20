package com.vodovoz.app.data.parser.response.ordering

import android.util.Log
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.DeliveryZonesBundleEntity
import com.vodovoz.app.data.parser.common.HistoryJsonParser.parseHistoryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ShippingIntervalsResponseJsonParser {

    fun ResponseBody.parseShippingIntervalsResponse(): ResponseEntity<List<ShippingIntervalEntity>> {
        val responseJson = JSONObject(string())
        Log.d(LogSettings.MAP_LOG, responseJson.toString())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> {
                var shippingIntervalEntityList = responseJson.getJSONObject("data").getJSONArray("DELIVERY_INTERVAL")
                    .parseShippingIntervalEntityList()
                if (shippingIntervalEntityList.size == 1 && shippingIntervalEntityList.first().id == 0L) {
                    shippingIntervalEntityList = listOf()
                }
                ResponseEntity.Success(shippingIntervalEntityList)
            }
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseShippingIntervalEntityList(): List<ShippingIntervalEntity> = mutableListOf<ShippingIntervalEntity>().also { list ->
        for (index in 0 until length()) list.add(getJSONObject(index).parseShippingIntervalEntity())
    }

    private fun JSONObject.parseShippingIntervalEntity() = ShippingIntervalEntity(
        id = getString("ID").toLong(),
        name = getString("NAME")
    )

}