package com.vodovoz.app.data.parser.response.cart

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.PriceEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.CartBundleEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.safeDouble
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

object CartResponseJsonParser {

    fun ResponseBody.parseCartResponse(): ResponseEntity<CartBundleEntity> {
        val responseJson = JSONObject(this.string())
     //   Log.d(LogSettings.RESPONSE_BODY_LOG, "--------------------------------------------------------CART")
     //   Log.d(LogSettings.RESPONSE_BODY_LOG, responseJson.toString(2))
        return ResponseEntity.Success(
            CartBundleEntity(
                giftMessageBottom = responseJson.getJSONArray("textkorzina").getMessageTextBasketOrNull("text_niz"),
                infoMessage = responseJson.getJSONArray("textkorzina").getMessageTextBasketOrNull("okno"),
                giftMessage = responseJson.getJSONArray("textkorzina").getMessageTextBasketOrNull("text"),
                availableProductEntityList = responseJson.getJSONArray("data").parseProductEntityList(),
                notAvailableProductEntityList = when(responseJson.has("netvnalichii")) {
                    true -> responseJson.getJSONObject("netvnalichii").parseNotAvailableProductEntityList()
                    false -> listOf()
                },
                giftProductEntityList = when(responseJson.isNull("osnovapodarki")) {
                    false -> try {
                        responseJson.getJSONObject("osnovapodarki").getJSONArray("PODARKI").parseGiftProductEntityList()
                    } catch (t: Throwable) {
                        listOf()
                    }
                    true -> listOf()
                },
                bestForYouCategoryDetailEntity = when(responseJson.has("tovary")) {
                    true -> responseJson.parseBestForYouCategoryDetailEntity()
                    false -> null
                }
            )
        )
    }

    private fun JSONArray.getMessageTextBasketOrNull(id: String) : MessageTextBasket? {
        var messageTextBasket: MessageTextBasket? = null
        for (index in 0 until length()) {
            if (getJSONObject(index).has("ID")) {
                if (getJSONObject(index).safeString("ID") == id) {
                    messageTextBasket = MessageTextBasket(
                        id = getJSONObject(index).safeString("ID"),
                        message = getJSONObject(index).safeString("MESSAGETEXT"),
                        color = getJSONObject(index).safeString("CVET"),
                        session = getJSONObject(index).safeString("SESION"),
                    )
                }
            }
        }
        return messageTextBasket
    }
    private fun JSONObject.parseNotAvailableProductEntityList() = when(isNull("data")) {
        true -> listOf()
        false -> getJSONArray("data").parseProductEntityList()
    }

    private fun JSONObject.parseBestForYouCategoryDetailEntity() = CategoryDetailEntity(
        name = getString("title"),
        productEntityList = getJSONArray("tovary").parseProductEntityList()
    )

    private fun JSONArray.parseGiftProductEntityList(): List<ProductEntity> = mutableListOf<ProductEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseGiftProductEntity())
        }
    }

    private fun JSONObject.parseGiftProductEntity() = ProductEntity(
        id = getString("ID").toLong(),
        name = getString("NAME"),
        detailPicture = getString("DETAIL_PICTURE").parseImagePath(),
        priceList = when(has("EXTENDED_PRICE")){
            true -> getJSONArray("EXTENDED_PRICE").parsePriceList()
            false -> listOf()
        },
        status = "",
        statusColor = "",
        rating = 0.0,
        commentAmount = "",
        isFavorite = false,
        depositPrice = 0,
        isGift = true,
        isBottle = false,
        detailPictureList = listOf(getString("DETAIL_PICTURE"))
    )

    private fun JSONArray.parsePriceList(): List<PriceEntity> = mutableListOf<PriceEntity>().also { list ->
        for (index in 0 until length()) list.add(getJSONObject(index).parsePriceEntity())
    }

    private fun JSONObject.parsePriceEntity() = PriceEntity(
        price = safeDouble("PRICE").roundToInt(),
        oldPrice = safeDouble("OLD_PRICE").roundToInt(),
        requiredAmount = safeInt("QUANTITY_FROM"),
        requiredAmountTo = safeInt("QUANTITY_TO")
    )

}

data class MessageTextBasket(
    val id : String? = null,
    val message : String? = null,
    val color : String? = null,
    val session : String? = null,
)