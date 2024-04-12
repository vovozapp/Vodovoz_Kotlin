package com.vodovoz.app.data.parser.response.order

import com.vodovoz.app.data.model.common.OrderDetailsEntity
import com.vodovoz.app.data.model.common.OrderPricesEntity
import com.vodovoz.app.data.model.common.OrderStatusEntity
import com.vodovoz.app.data.model.common.PriceEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.parser.common.safeStringConvertToBoolean
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object OrderDetailsResponseJsonParser {

    fun ResponseBody.parseOrderDetailsResponse(): ResponseEntity<OrderDetailsEntity> {
        val responseJson = JSONObject(this.string())
        return when (responseJson.getString("status")) {
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
        //productsPrice = safeInt("PRICE_GOODS_ITOGO"),//getString("PRICE_GOODS_ITOGO").toDouble().toInt(),
        depositPrice = safeInt("ZALOG_ITOGO"),//getString("ZALOG_ITOGO").toDouble().toInt(),
        totalPrice = getString("PRICE").toDouble().toInt(),
        //deliveryPrice = getString("PRICE_DELIVERY").toDouble().toInt(),
        userFirstName = getString("USER_NAME"),
        userSecondName = getString("USER_LAST_NAME"),
        userPhone = getString("PHONE"),
        address = getString("ADDRESS"),
        deliveryTimeInterval = getString("INTERVAL"),
        isPayed = when (getString("PAYED")) {
            "N" -> false
            "Y" -> true
            else -> throw Exception("Unknown payed status")
        },
        status = OrderStatusEntity(
            id = safeString("STATUS_NAME_ID"),
            statusName = safeString("STATUS_NAME")
        ),
        payUri = if (has("PAYSISTEM") && getJSONObject("PAYSISTEM").has("URL")) {
            getJSONObject("PAYSISTEM").getString("URL")
        } else {
            "Нет данных по оплате"
        },
        payMethod = if (has("PAYSISTEM") && getJSONObject("PAYSISTEM").has("NAME")) {
            getJSONObject("PAYSISTEM").getString("NAME")
        } else {
            "Нет данных по оплате"
        },
        productEntityList = getJSONArray("ITEMS").parseProductEntityList(),
        driverId = if (has("VODILA") && !isNull("VODILA")) {
            getJSONObject("VODILA").safeString("IDVODILA")
        } else null,
        driverName = if (has("VODILA") && !isNull("VODILA")) {
            getJSONObject("VODILA").safeString("NAME")
        } else null,
        driverUrl = if (has("VODILA") &&!isNull("VODILA")) {
            getJSONObject("VODILA").safeString("SSILKAPEREDACH")
        } else null,
        orderPricesEntityList = if (has("ITOG_DANNYE")) {
            getJSONArray("ITOG_DANNYE").parseOrderPricesList()
        } else listOf(),
        repeatOrder = if (has("POVTOR_ZAKAZA")) {
            safeString("POVTOR_ZAKAZA")  == "Y"
        } else true
    )

    private fun JSONArray.parseProductEntityList(): List<ProductEntity> =
        mutableListOf<ProductEntity>().apply {
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

        val detailPicture = safeString("DETAIL_PICTURE")

        return ProductEntity(
            id = when (has("PRODUCT_ID")) {
                true -> getLong("PRODUCT_ID")
                false -> getLong("ID")
            },
            name = safeString("NAME"),
            detailPicture = detailPicture.parseImagePath(),
            priceList = listOf(
                PriceEntity(
                    price = safeInt("PRICE"),
                    oldPrice = 0,
                    requiredAmount = 0,
                    0
                )
            ),
            rating = when (has("")) {
                true -> getDouble("PROPERTY_RATING_VALUE")
                else -> 0.0
            },
            status = status,
            statusColor = statusColor,
            commentAmount = when (has("COUTCOMMENTS")) {
                true -> safeString("COUTCOMMENTS")
                false -> ""
            },
            orderQuantity = getInt("QUANTITY"),
            isFavorite = when (has("FAVORITE")) {
                true -> getBoolean("FAVORITE")
                false -> false
            },
            detailPictureList = parseDetailPictureList(detailPicture),
            depositPrice = 0,
            isBottle = when (has("CATALOG")) {
                true -> when (getJSONObject("CATALOG").getLong("IBLOCK_ID")) {
                    90L -> true
                    else -> false
                }

                false -> {
                    if (has("IBLOCK_ID")) {
                        val idBlock = getLong("IBLOCK_ID")
                        idBlock == 90L
                    } else {
                        false
                    }
                }
            },
            isGift = when (has("CATALOG")) {
                true -> when (getJSONObject("CATALOG").getLong("IBLOCK_ID")) {
                    26L -> true
                    else -> false
                }

                false -> {
                    if (has("IBLOCK_ID")) {
                        val idBlock = getLong("IBLOCK_ID")
                        idBlock == 26L
                    } else {
                        false
                    }
                }
            },
            chipsBan = when (has("ZAPRET_FISHKAM")) {
                true -> safeInt("ZAPRET_FISHKAM")
                false -> null
            },
            leftItems = safeInt("CATALOG_QUANTITY"),
            isAvailable = safeStringConvertToBoolean("ACTIVE"),
        )
    }

    private fun JSONObject.parseIsCanReturnBottles(): Boolean {
        val categoryIdList = mutableListOf<Int>()
        val categoryIdJsonArray = getJSONArray("SECTION_ID")

        for (index in 0 until categoryIdJsonArray.length()) {
            categoryIdList.add(categoryIdJsonArray.getInt(index))
        }

        return when (categoryIdList.find { it == 2991 || it == 2990 || it == 75 }) {
            null -> false
            else -> true
        }
    }

    private fun JSONObject.parseDetailPictureList(
        detailPicture: String,
    ): List<String> = mutableListOf<String>().apply {
        add(detailPicture.parseImagePath())
        if (has("MORE_PHOTO")) {
            val detailPictureJSONArray = getJSONObject("MORE_PHOTO").getJSONArray("VALUE")
            for (index in 0 until detailPictureJSONArray.length()) {
                add(detailPictureJSONArray.getString(index).parseImagePath())
            }
        }
    }

    private fun JSONArray.parseOrderPricesList(): List<OrderPricesEntity> =
        mutableListOf<OrderPricesEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseOrderPrices())
            }
        }

    private fun JSONObject.parseOrderPrices(): OrderPricesEntity {
        return OrderPricesEntity(
            name = safeString("NAME"),
            result = safeString("RESULT")
        )
    }

}