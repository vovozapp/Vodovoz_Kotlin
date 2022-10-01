package com.vodovoz.app.data.parser.response.shipping

import com.vodovoz.app.data.model.common.PayMethodEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.ShippingIntervalEntity
import com.vodovoz.app.data.model.features.ShippingInfoBundleEntity
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ShippingInfoResponseJsonParser {

    fun ResponseBody.parseShippingInfoResponse(): ResponseEntity<ShippingInfoBundleEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(ShippingInfoBundleEntity(
                id = responseJson.getJSONObject("data").getString("ID").toLong(),
                parkingPrice = when(responseJson.getJSONObject("data").isNull("PARKOVKA_SZORIN")) {
                    false -> responseJson.getJSONObject("data").getJSONObject("PARKOVKA_SZORIN").safeInt("PRICE")
                    true -> 0
                },
                extraShippingPrice = responseJson.getJSONObject("data").safeInt("NACENKA_DOSTAVKA"),
                commonShippingPrice = responseJson.getJSONObject("data").safeInt("OBICH_DOSTAVKA_PRICE"),
                shippingPrice = responseJson.getJSONObject("data").safeInt("DELIVERY_PRICE"),
                name = responseJson.getJSONObject("data").safeString("NAME"),
                todayShippingPrice = responseJson.getJSONObject("data").safeInt("SROCHNAYA_DELIVERY_PRICE_SZORIN"),
                todayShippingInfo = responseJson.getJSONObject("data").safeString("TEXT_SROCHNAYA_DELIVERY_PRICE_SZORIN"),
                payMethodEntityList = responseJson.getJSONArray("paysystem").parsePayMethodEntityList(),
                shippingIntervalEntityList = responseJson.getJSONObject("data")
                    .getJSONArray("DELIVERY_INTERVAL")
                    .parseShippingIntervalEntityList(),
            ))
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseShippingIntervalEntityList(): List<ShippingIntervalEntity> = mutableListOf<ShippingIntervalEntity>().also { list ->
        for (index in 0 until length()) {
            val shippingIntervalEntity = getJSONObject(index).parseShippingIntervalEntity()
            if (shippingIntervalEntity.id != 0L) list.add(shippingIntervalEntity)
        }

    }

    private fun JSONObject.parseShippingIntervalEntity() = ShippingIntervalEntity(
        id = getString("ID").toLong(),
        name = getString("NAME")
    )

    private fun JSONArray.parsePayMethodEntityList(): List<PayMethodEntity> = mutableListOf<PayMethodEntity>().also { list ->
        for (index in 0 until length()) list.add(getJSONObject(index).parsePayMethodEntity())
    }

    private fun JSONObject.parsePayMethodEntity() = PayMethodEntity(
        id = getString("ID").toLong(),
        name = getString("NAME")
    )

}