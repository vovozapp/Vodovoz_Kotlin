package com.vodovoz.app.data.parser.response.map

import com.vodovoz.app.data.model.common.DeliveryZoneEntity
import com.vodovoz.app.data.model.common.HistoryEntity
import com.vodovoz.app.data.model.common.PointEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.DeliveryZonesBundleEntity
import com.vodovoz.app.data.parser.common.HistoryJsonParser.parseHistoryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object DeliveryZonesBundleResponseJsonParser {

    fun ResponseBody.parseDeliveryZonesBundleResponse(): ResponseEntity<DeliveryZonesBundleEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                DeliveryZonesBundleEntity(
                    aboutDeliveryTimeUrl = responseJson.getJSONObject("text").getString("URL"),
                    deliveryZoneEntityList = responseJson.getJSONArray("data").parseDeliveryZoneEntityList()
                )
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseDeliveryZoneEntityList(): List<DeliveryZoneEntity> = mutableListOf<DeliveryZoneEntity>().also { list ->
        for (index in 0 until length()) {
            list.add(getJSONObject(index).parseDeliveryZoneEntity())
        }
    }
    private fun JSONObject.parseDeliveryZoneEntity() = DeliveryZoneEntity(
        deliveryTime = getString("TEXT"),
        color = getString("COLOR"),
        pointEntityList = getJSONArray("TOCHKA").parsePointEntityList()
    )

    private fun JSONArray.parsePointEntityList(): List<PointEntity> = mutableListOf<PointEntity>().also { list ->
        for (index in 0 until length()) {
            list.add(getJSONArray(index).parsePointEntity())
        }
    }
    private fun JSONArray.parsePointEntity() = PointEntity(
        latitude = getDouble(0),
        longitude = getDouble(1)
    )

}