package com.vodovoz.app.data.parser.response.order

import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeStringConvertToBoolean
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object OrderDetailsResponseJsonParser {

    fun ResponseBody.parseOrderDetailsResponse(): ResponseEntity<OrderDetailsEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").getJSONObject(0).parseOrderDetailsEntity()
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseOrderDetailsEntity() = OrderDetailsEntity(
        id = getString("ID").toLong(),
        dateOrder = getString("DATE_START"),
        dateDelivery = getString("DATE_OUT"),
        productsPrice = safeInt("PRICE_GOODS_ITOGO"),//getString("PRICE_GOODS_ITOGO").toDouble().toInt(),
        depositPrice = safeInt("ZALOG_ITOGO"),//getString("ZALOG_ITOGO").toDouble().toInt(),
        totalPrice = getString("PRICE").toDouble().toInt(),
        deliveryPrice = getString("PRICE_DELIVERY").toDouble().toInt(),
        userFirstName = getString("USER_NAME"),
        userSecondName = getString("USER_LAST_NAME"),
        userPhone = getString("PHONE"),
        address = getString("ADDRESS"),
        deliveryTimeInterval = getString("INTERVAL"),
        isPayed = when(getString("PAYED")) {
            "N" -> false
            "Y" -> true
            else -> throw Exception("Unknown payed status")
        },
        status = OrderStatusEntity.fromId(getString("STATUS_NAME_ID")),
        payUri = when(getJSONObject("PAYSISTEM").has("URL")) {
            true -> getJSONObject("PAYSISTEM").getString("URL")
            false -> ""
        },
        payMethod = getJSONObject("PAYSISTEM").getString("NAME"),
        productEntityList = getJSONArray("ITEMS").parseProductEntityList()
    )

    private fun JSONArray.parseProductEntityList(): List<ProductEntity> = mutableListOf<ProductEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseProductEntity())
        }
    }

    fun JSONObject.parseProductEntity(): ProductEntity {
        var status = ""
        var statusColor = ""

        if (has("NALICHIE")) {
            if (get("NALICHIE") != JSONObject.NULL) {
                val statusJson = getJSONObject("NALICHIE")
                status = statusJson.getString("NAME")
                statusColor = statusJson.getString("CVET")
            }
        }

        val detailPicture = getString("DETAIL_PICTURE")

        return ProductEntity(
            id = when(has("PRODUCT_ID")) {
                true -> getLong("PRODUCT_ID")
                false -> getLong("ID")
            },
            name = getString("NAME"),
            detailPicture = detailPicture.parseImagePath(),
            priceList = listOf(PriceEntity(price = getInt("PRICE"), oldPrice = 0, requiredAmount = 0)),
            rating = when(has("")) {
                true -> getDouble("PROPERTY_RATING_VALUE")
                else -> 0.0
            },
            status = status,
            statusColor = statusColor,
            commentAmount = when(has("COUTCOMMENTS")) {
                true -> getString("COUTCOMMENTS")
                false -> ""
            },
            orderQuantity = getInt("QUANTITY"),
            isFavorite = when(has("FAVORITE")) {
                true -> getBoolean("FAVORITE")
                false -> false
            },
            detailPictureList = parseDetailPictureList(detailPicture),
            depositPrice = 0,
            leftItems = safeInt("CATALOG_QUANTITY"),
            isAvailable = safeStringConvertToBoolean("ACTIVE")
        )
    }

    private fun JSONObject.parseIsCanReturnBottles(): Boolean {
        val categoryIdList = mutableListOf<Int>()
        val categoryIdJsonArray = getJSONArray("SECTION_ID")

        for (index in 0 until categoryIdJsonArray.length()) {
            categoryIdList.add(categoryIdJsonArray.getInt(index))
        }

        return when(categoryIdList.find { it == 2991 || it == 2990 || it == 75 }) {
            null -> false
            else -> true
        }
    }

    private fun JSONObject.parseDetailPictureList(
        detailPicture: String
    ): List<String> = mutableListOf<String>().apply {
        add(detailPicture.parseImagePath())
        if (has("MORE_PHOTO")) {
            val detailPictureJSONArray = getJSONObject("MORE_PHOTO").getJSONArray("VALUE")
            for (index in 0 until detailPictureJSONArray.length()) {
                add(detailPictureJSONArray.getString(index).parseImagePath())
            }
        }
    }

}