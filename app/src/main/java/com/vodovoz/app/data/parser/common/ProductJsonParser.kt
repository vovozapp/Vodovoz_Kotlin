package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.LabelEntity
import com.vodovoz.app.data.model.common.PriceEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import org.json.JSONArray
import org.json.JSONObject

object ProductJsonParser {

    fun JSONArray.parseProductEntityList(forCart: Boolean = false): List<ProductEntity> =
        mutableListOf<ProductEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseProductEntity(forCart))
            }
        }

    private fun JSONObject.parseProductEntity(forCart: Boolean = false): ProductEntity {
        var status = ""
        var statusColor = ""

        if (has("NALICHIE")) {
            if (get("NALICHIE") != JSONObject.NULL) {
                val statusJson = getJSONObject("NALICHIE")
                status = statusJson.getString("NAME")
                statusColor = statusJson.getString("CVET")
            }
        }

        val labels = mutableListOf<LabelEntity>()
        if (has("NALICHIE_MORE") && !isNull("NALICHIE_MORE")) {
            val labelList = getJSONArray("NALICHIE_MORE")
            for (index in 0 until labelList.length()) {
                labels.add(
                    LabelEntity(
                        labelList.getJSONObject(index).safeString("NAME").trim(),
                        labelList.getJSONObject(index).safeString("CVET")
                    )
                )
            }
        }


        val detailPicture = getString("DETAIL_PICTURE")

        return ProductEntity(
            id = when (has("PRODUCT_ID")) {
                true -> getLong("PRODUCT_ID")
                false -> getLong("ID")
            },
            name = getString("NAME"),
            detailPicture = detailPicture.parseImagePath(),
            priceList = getJSONArray("EXTENDED_PRICE").parsePriceList(),
            rating = when (has("PROPERTY_RATING_VALUE")) {
                true -> getDouble("PROPERTY_RATING_VALUE")
                else -> 0.0
            },
            status = status,
            statusColor = statusColor,
            labels = labels.toList(),
            commentAmount = when (has("COUTCOMMENTS")) {
                true -> getString("COUTCOMMENTS")
                false -> ""
            },
            cartQuantity = when (has("QUANTITY")) {
                true -> getInt("QUANTITY")
                false -> 0
            },
            isFavorite = when (has("FAVORITE")) {
                true -> getBoolean("FAVORITE")
                false -> false
            },
            detailPictureList = parseDetailPictureList(detailPicture),
            depositPrice = when (has("PROPERTY_ZALOGCIFR_VALUE")) {
                true -> safeInt("PROPERTY_ZALOGCIFR_VALUE")
                false -> when (isNull("PROPERTY_ZALOG_VALUE")) {
                    true -> 0
                    false -> when (safeString("PROPERTY_ZALOG_VALUE").filter { it.isDigit() }
                        .isEmpty()) {
                        true -> 0
                        false -> {
                            val zal = safeString("PROPERTY_ZALOG_VALUE").filter { it.isDigit() }
                            if (zal.isNotEmpty()) {
                                zal.toInt()
                            } else {
                                0
                            }
                        }
                    }
                }
            },
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
            leftItems = when (has("CATALOG_QUANTITY")) {
                true -> safeInt("CATALOG_QUANTITY")
                false -> when (has("CATALOG")) {
                    true -> getJSONObject("CATALOG").safeInt("CATALOG_QUANTITY")
                    false -> 0
                }
            },
            pricePerUnit = if (has("DOPTSENA_ZA_EDINICY")) {
                safeString("DOPTSENA_ZA_EDINICY")
            } else if (has("PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE") && safeInt("PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE") != 0) {
                StringBuilder()
                    .append(safeInt("PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE"))
                    .append(" ₽/кг")
                    .toString()
            } else {
                ""
            },
            replacementProductEntityList = when (has("nettovar") && !isNull("nettovar")) {
                false -> listOf()
                true -> getJSONObject("nettovar").getJSONArray("data").parseProductEntityList()
            },
            chipsBan = when (has("ZAPRET_FISHKAM")) {
                true -> safeInt("ZAPRET_FISHKAM")
                false -> null
            },
            totalDisc = safeDouble("TOTAL_SKIDKA"),
            conditionalPrice = when (has("NewPrice")) {
                true -> getJSONObject("NewPrice").safeString("Price")
                false -> ""
            },
            condition = when (has("NewPrice")) {
                true -> getJSONObject("NewPrice").safeString("DescPrice")
                false -> ""
            },
            forCart = forCart,
            giftText = safeString("PODAROK_KORZINA"),
        )
    }

    private fun JSONArray.parsePriceList(): List<PriceEntity> =
        mutableListOf<PriceEntity>().also { list ->
            for (index in 0 until length()) list.add(getJSONObject(index).parsePriceEntity())
        }

    private fun JSONObject.parsePriceEntity() = PriceEntity(
        price = safeDouble("PRICE"),
        oldPrice = safeDouble("OLD_PRICE"),
        requiredAmount = safeInt("QUANTITY_FROM"),
        requiredAmountTo = safeInt("QUANTITY_TO")
    )

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

}