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
            id = getLong("ID"),
            name = getString("NAME"),
            detailPicture = detailPicture.parseImagePath(),
            newPrice = getJSONArray("EXTENDED_PRICE").getJSONObject(0).getInt("PRICE"),
            oldPrice = getJSONArray("EXTENDED_PRICE").getJSONObject(0).getInt("OLD_PRICE"),
            rating = getDouble("PROPERTY_RATING_VALUE"),
            status = status,
            statusColor = statusColor,
            commentAmount = when(has("COUTCOMMENTS")) {
                true -> getString("COUTCOMMENTS")
                false -> ""
            },
            detailPictureList = parseDetailPictureList(detailPicture)
        )
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