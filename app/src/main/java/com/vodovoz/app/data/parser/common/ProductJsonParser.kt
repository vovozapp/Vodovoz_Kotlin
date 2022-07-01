package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import org.json.JSONArray
import org.json.JSONObject

object ProductJsonParser {

    fun JSONArray.parseProductEntityList(): List<ProductEntity> = mutableListOf<ProductEntity>().apply {
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
            newPrice = getJSONArray("EXTENDED_PRICE").getJSONObject(0).getInt("PRICE"),
            oldPrice = getJSONArray("EXTENDED_PRICE").getJSONObject(0).getInt("OLD_PRICE"),
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
            cartQuantity = when(has("QUANTITY")) {
                true -> getInt("QUANTITY")
                false -> 0
            },
            detailPictureList = parseDetailPictureList(detailPicture),
            isCanReturnBottles = when(has("CATALOG")) {
                true -> getJSONObject("CATALOG").parseIsCanReturnBottles()
                false -> false
            }
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