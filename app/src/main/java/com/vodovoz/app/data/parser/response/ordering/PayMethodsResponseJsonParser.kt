package com.vodovoz.app.data.parser.response.ordering

import com.vodovoz.app.data.model.common.HistoryEntity
import com.vodovoz.app.data.model.common.PayMethodEntity
import com.vodovoz.app.data.model.common.PointEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.DeliveryZonesBundleEntity
import com.vodovoz.app.data.parser.common.HistoryJsonParser.parseHistoryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object PayMethodsResponseJsonParser {

    fun ResponseBody.parsePayMethodsResponse(): ResponseEntity<List<PayMethodEntity>> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("paysystem").parsePayMethodEntityList())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parsePayMethodEntityList(): List<PayMethodEntity> = mutableListOf<PayMethodEntity>().also { list ->
        for (index in 0 until length()) list.add(getJSONObject(index).parsePayMethodEntity())
    }

    private fun JSONObject.parsePayMethodEntity() = PayMethodEntity(
        id = getString("ID").toLong(),
        name = getString("NAME")
    )

}