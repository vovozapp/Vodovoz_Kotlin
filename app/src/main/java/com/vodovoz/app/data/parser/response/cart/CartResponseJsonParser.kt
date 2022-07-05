package com.vodovoz.app.data.parser.response.cart

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.CartBundleEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object CartResponseJsonParser {

    fun ResponseBody.parseCartResponse(): ResponseEntity<CartBundleEntity> {
        val responseJson = JSONObject(this.string())
        return ResponseEntity.Success(
            CartBundleEntity(
                giftMessage = when(responseJson.getJSONArray("textkorzina").getJSONObject(0).isNull("MESSAGETEXT")) {
                    true -> null
                    false -> responseJson.getJSONArray("textkorzina").getJSONObject(0).getString("MESSAGETEXT")
                },
                availableProductEntityList = responseJson.getJSONArray("data").parseProductEntityList(),
                notAvailableProductEntityList = when(responseJson.has("netvnalichii")) {
                    true -> responseJson.getJSONObject("netvnalichii").parseNotAvailableProductEntityList()
                    false -> listOf()
                },
                giftProductEntityList = when(responseJson.isNull("osnovapodarki")) {
                    false -> responseJson.getJSONObject("osnovapodarki").getJSONArray("PODARKI").parseGiftProductEntityList()
                    true -> listOf()
                },
                bestForYouCategoryDetailEntity = when(responseJson.has("tovary")) {
                    true -> responseJson.parseBestForYouCategoryDetailEntity()
                    false -> null
                }
            )
        )
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
        oldPrice = 0,
        newPrice = 0,
        status = "",
        statusColor = "",
        rating = 0.0,
        commentAmount = "",
        isFavorite = false,
        detailPictureList = listOf(getString("DETAIL_PICTURE"))
    )

}