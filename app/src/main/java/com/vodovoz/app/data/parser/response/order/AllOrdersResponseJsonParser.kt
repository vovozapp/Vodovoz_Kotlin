package com.vodovoz.app.data.parser.response.order

import com.vodovoz.app.data.model.common.OrderEntity
import com.vodovoz.app.data.model.common.OrderFilterEntity
import com.vodovoz.app.data.model.common.OrderListEntity
import com.vodovoz.app.data.model.common.OrderStatusEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.parser.common.safeStringConvertToBoolean
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object AllOrdersResponseJsonParser {

    fun ResponseBody.parseAllOrdersSliderResponse(): ResponseEntity<OrderListEntity> {
        val responseJson = JSONObject(this.string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                OrderListEntity(
                    orderFilters = responseJson.getJSONArray("filterstatus").parseOrderFilters(),
                    orders = responseJson.getJSONArray("data").parseOrderEntityList(),
                )
            )

            else -> ResponseEntity.Success(
                OrderListEntity(
                    title = responseJson.safeString("title"),
                    message = responseJson.safeString("message"),
                )
            )
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
        status = OrderStatusEntity(
            id = safeString("STATUS_ID"),
            statusName = safeString("STATUS_NAME")
        ),
        address = getString("ADDRESS"),
        date = getString("DATE_INSERT"),
        productEntityList = getJSONArray("ITEMS").parseProductEntityList(),
        repeatOrder = if (has("POVTOR_ZAKAZA")) {
            safeString("POVTOR_ZAKAZA") == "Y"
        } else true
    )

    private fun JSONArray.parseProductEntityList(): List<ProductEntity> =
        mutableListOf<ProductEntity>().also { list ->
            for (index in 0 until length()) {
                list.add(getJSONObject(index).parseProductEntity())
            }
        }

    private fun JSONObject.parseProductEntity() = ProductEntity(
        id = getLong("ID"),
        detailPicture = getString("DETAIL_PICTURE").parseImagePath(),
        isAvailable = safeStringConvertToBoolean("ACTIVE")
    )

}

private fun JSONArray.parseOrderFilters(): List<OrderFilterEntity> {

    return mutableListOf<OrderFilterEntity>().also { list ->
        for (index in 0 until length()) {
            list.add(getJSONObject(index).parseOrderFilter())
        }
    }
}

private fun JSONObject.parseOrderFilter() = OrderFilterEntity(
    id = getString("ID"),
    name = getString("NAME"),

    )
