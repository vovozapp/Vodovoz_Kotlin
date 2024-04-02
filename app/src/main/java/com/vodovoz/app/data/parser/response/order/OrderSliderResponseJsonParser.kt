package com.vodovoz.app.data.parser.response.order

import com.vodovoz.app.data.model.common.OrderEntity
import com.vodovoz.app.data.model.common.OrderStatusEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import com.vodovoz.app.util.extensions.debugLog
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object OrderSliderResponseJsonParser {

    fun ResponseBody.parseOrderSliderResponse(): ResponseEntity<List<OrderEntity>> {
        val responseJson = JSONObject(this.string())
        debugLog { LogSettings.RESPONSE_BODY_LOG + " ${responseJson.toString(2)}" }
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").parseOrderEntityList()
            )
            else -> ResponseEntity.Error("Ошибка парсинга заказы")
        }
    }

    private fun JSONArray.parseOrderEntityList(): List<OrderEntity> =
        mutableListOf<OrderEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseOrderEntity())
            }
        }

    private fun JSONObject.parseOrderEntity() = OrderEntity(
        id = getLong("ID"),
        price = getInt("PRICE"),
        date = safeString("DATE_OUT"),
        status = OrderStatusEntity.fromId(getString("STATUS_NAME_ID")),
        address = getString("ADRESSDOSTAVKI"),
        repeatOrder = if (has("POVTOR_ZAKAZA")) {
            safeString("POVTOR_ZAKAZA")  == "Y"
        } else true
    )

}