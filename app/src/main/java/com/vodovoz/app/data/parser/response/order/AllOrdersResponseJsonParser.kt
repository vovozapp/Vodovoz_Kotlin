package com.vodovoz.app.data.parser.response.order

import com.vodovoz.app.data.model.common.OrderEntity
import com.vodovoz.app.data.model.common.OrderStatusEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object AllOrdersResponseJsonParser {

    fun ResponseBody.parseAllOrdersSliderResponse(): ResponseEntity<List<OrderEntity>> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONArray("data").parseOrderEntityList())
            else -> ResponseEntity.Success(listOf())
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
        status = OrderStatusEntity.fromId(getString("STATUS_ID")),
        address = getString("ADDRESS"),
        productEntityList = getJSONArray("ITEMS").parseProductEntityList()
    )

    private fun JSONArray.parseProductEntityList(): List<ProductEntity> = mutableListOf<ProductEntity>().also { list ->
        for (index in 0 until length()) {
            list.add(getJSONObject(index).parseProductEntity())
        }
    }

    private fun JSONObject.parseProductEntity() = ProductEntity(
        id = getLong("ID"),
        detailPicture = getString("DETAIL_PICTURE").parseImagePath()
    )

}