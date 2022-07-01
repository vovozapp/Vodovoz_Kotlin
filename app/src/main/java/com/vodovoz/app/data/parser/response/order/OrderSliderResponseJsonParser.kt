package com.vodovoz.app.data.parser.response.order

import com.vodovoz.app.data.model.common.OrderEntity
import com.vodovoz.app.data.model.common.OrderStatus
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object OrderSliderResponseJsonParser {

    fun ResponseBody.parseOrderSliderResponse(): ResponseEntity<List<OrderEntity>> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONArray("data").parseOrderEntityList())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseOrderEntityList(): List<OrderEntity> = mutableListOf<OrderEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseOrderEntity())
        }
    }

    private fun JSONObject.parseOrderEntity() = OrderEntity(
        id = getLong("ID"),
        price = getInt("PRICE"),
        status = OrderStatus.fromId(getString("STATUS_NAME_ID")),
        address = getString("ADRESSDOSTAVKI")
    )

}