package com.vodovoz.app.data.parser.response.product

import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.parser.common.BrandJsonParser.parserBrandEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.safeDouble
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.parser.response.comment.AllCommentsByProductResponseJsonParser
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

object ProductDetailsResponseJsonParser {

    fun ResponseBody.parseProductDetailsResponse(): ResponseEntity<ProductDetailsBundleEntity> {
        val responseJson = JSONObject(this.string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                ProductDetailsBundleEntity(
                    productDetailEntity = responseJson.getJSONObject("data")
                        .parseProductDetailEntity(
                            shareUrl = responseJson.getString("detail_page_url"),
                            commentsAmount = when (responseJson.isNull("comments")) {
                                true -> 0
                                else -> {
                                    val count = responseJson.getJSONObject("comments")
                                        .safeString("COMMENT_COUNT")
                                    if (count.isNotEmpty()) {
                                        count.toInt()
                                    } else {
                                        0
                                    }
                                }
                            }
                        ),
                    categoryEntity = responseJson.getJSONArray("razdel")
                        .getJSONObject(0).parseCategoryEntity(),
                    commentImages = when (responseJson.isNull("comments")) {
                        true -> listOf()
                        else -> when (responseJson.getJSONObject("comments").isNull("KARTINKI")) {
                            true -> listOf()
                            else -> responseJson.getJSONObject("comments").getJSONArray("KARTINKI")
                                .parseCommentImageEntityList()
                        }
                    },
                    commentEntityList = when (responseJson.isNull("comments")) {
                        true -> listOf()
                        else -> responseJson.getJSONObject("comments")
                            .getJSONArray("COMENTS").parseCommentEntityList()
                    },
                    maybeLikeProductEntityList = responseJson.getJSONObject("novinki")
                        .getJSONArray("REKOMEND").parseProductEntityList(),
                    buyWithProductEntityList = responseJson.getJSONObject("rekomend")
                        .getJSONArray("REKOMEND").parseProductEntityList(),
                    recommendProductEntityList = responseJson.getJSONObject("tovar")
                        .getJSONArray("REKOMEND").parseProductEntityList(),
                    searchWordList = responseJson.getJSONObject("klyshslova")
                        .getJSONArray("USHYTZAPROS").parseSearchWordList(),
                    promotionEntityList = responseJson.getJSONObject("action")
                        .getJSONArray("REKOMEND").parsePromotionEntityList(),
                    serviceEntityList = when (responseJson.isNull("yslugi")) {
                        true -> listOf()
                        false -> responseJson.getJSONObject("yslugi")
                            .getJSONArray("OSNOVA").parseServiceEntityList()
                    },
                    replacementProductsCategoryDetail = when (responseJson.isNull("nettovar")) {
                        true -> null
                        false -> responseJson.getJSONObject("nettovar")
                            .parseReplacementProductsCategoryDetailEntity()
                    }
                )
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    //Category
    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
        id = getString("ID").toLong(),
        name = getString("NAME"),
        detailPicture = getString("DETAIL_PICTURE").parseImagePath()
    )

    //ProductDetail
    private fun JSONObject.parseProductDetailEntity(
        commentsAmount: Int,
        shareUrl: String,
    ): ProductDetailEntity {
        var status = ""
        var statusColor = ""
        if (!isNull("NALICHIE")) {
            statusColor = getJSONObject("NALICHIE").getString("CVET")
            status = getJSONObject("NALICHIE").getString("NAME")
        }

        val detailPictureList = mutableListOf(getString("DETAIL_PICTURE").parseImagePath())
        if (has("MORE_PHOTO")) {
            detailPictureList.addAll(
                getJSONObject("MORE_PHOTO").getJSONArray("VALUE").parseDetailPictureList()
            )
        }

        var youtubeVideoCode = ""
        if (has("YOUTUBE_VIDEO")) {
            youtubeVideoCode = getJSONArray("YOUTUBE_VIDEO").getJSONObject(0).getString("VIDEO")
        }

        var brandEntity: BrandEntity? = null
        if (!isNull("BRAND")) {
            brandEntity = getJSONObject("BRAND").parserBrandEntity()
        }

        return ProductDetailEntity(
            id = getString("ID").toLong(),
            name = getString("NAME"),
            shareUrl = shareUrl,
            previewText = getString("PREVIEW_TEXT"),
            detailText = getString("DETAIL_TEXT"),
            isFavorite = getBoolean("FAVORITE"),
            rating = getDouble("PROPERTY_rating_VALUE"),
            youtubeVideoCode = youtubeVideoCode,
            consumerInfo = getJSONObject("INFORMATIONS").getString("ZNACHENIE"),
            priceEntityList = getJSONArray("EXTENDED_PRICE").parsePriceEntityList(),
            commentsAmount = commentsAmount,
            statusColor = statusColor,
            status = status,
            propertiesGroupEntityList = getJSONArray("PROP").parsePropertyGroupEntityList(),
            detailPictureList = detailPictureList,
            brandEntity = brandEntity,
            isAvailable = true,
            leftItems = when (has("CATALOG_QUANTITY")) {
                true -> safeInt("CATALOG_QUANTITY")
                false -> when (has("CATALOG")) {
                    true -> getJSONObject("CATALOG").safeInt("CATALOG_QUANTITY")
                    false -> 0
                }
            },
            pricePerUnit =if (has("DOPTSENA_ZA_EDINICY")) {
                safeString("DOPTSENA_ZA_EDINICY")
            } else if (has("PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE") && safeInt("PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE") != 0) {
                StringBuilder()
                    .append(safeInt("PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE"))
                    .append(" ₽/кг")
                    .toString()
            } else {
                ""
            }
        )
    }

    //PropertyGroup
    private fun JSONArray.parsePropertyGroupEntityList(): List<PropertiesGroupEntity> =
        mutableListOf<PropertiesGroupEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parsePropertyGroupEntity())
            }
        }

    private fun JSONObject.parsePropertyGroupEntity() = PropertiesGroupEntity(
        name = getString("NAMES"),
        propertyEntityList = getJSONArray("HARAKTERISTIK").parsePropertyEntityList()
    )

    //Property
    private fun JSONArray.parsePropertyEntityList(): List<PropertyEntity> =
        mutableListOf<PropertyEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parsePropertyEntity())
            }
        }

    private fun JSONObject.parsePropertyEntity() = PropertyEntity(
        name = getString("NAME"),
        value = getString("VALUE")
    )

    //DetailPicture
    private fun JSONArray.parseDetailPictureList(): List<String> = mutableListOf<String>().apply {
        for (index in 0 until length()) {
            add(getString(index).parseImagePath())
        }
    }

    //Price
    private fun JSONArray.parsePriceEntityList(): List<PriceEntity> =
        mutableListOf<PriceEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parsePriceEntity())
            }
        }

    private fun JSONObject.parsePriceEntity() = PriceEntity(
        price = safeDouble("PRICE").roundToInt(),
        oldPrice = safeDouble("OLD_PRICE").roundToInt(),
        requiredAmount = safeInt("QUANTITY_FROM"),
        requiredAmountTo = safeInt("QUANTITY_TO")
    )

    //Service
    private fun JSONArray.parseServiceEntityList(): List<ServiceEntity> =
        mutableListOf<ServiceEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseServiceEntity())
            }
        }

    private fun JSONObject.parseServiceEntity() = ServiceEntity(
        name = getString("NAME"),
        price = getString("OPISANIE"),
        detailPicture = getString("IMAGE"),
        type = getString("TIP")
    )

    //Comment
    private fun JSONArray.parseCommentEntityList(): List<CommentEntity> =
        mutableListOf<CommentEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseCommentEntity())
            }
        }

    private fun JSONObject.parseCommentEntity() = CommentEntity(
        author = if (has("NAME")) getString("NAME") else null,
        text = getString("TEXT"),
        date = getString("DATA"),
        authorPhoto = if (has("USER_PHOTO")) getString("USER_PHOTO").parseImagePath() else null,
        images = when (has("IMAGES") && !isNull("IMAGES")) {
            false -> null
            true -> getJSONArray("IMAGES").parseCommentImageEntityList()
        }
    )

    private fun JSONArray.parseCommentImageEntityList(): List<AllCommentsByProductResponseJsonParser.CommentImageEntity> =
        mutableListOf<AllCommentsByProductResponseJsonParser.CommentImageEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseCommentImageEntity())
            }
        }

    private fun JSONObject.parseCommentImageEntity() =
        AllCommentsByProductResponseJsonParser.CommentImageEntity(
            ID = safeString("ID"),
            FILE_ID = safeString("FILE_ID"),
            POST_ID = safeString("POST_ID"),
            BLOG_ID = safeString("BLOG_ID"),
            USER_ID = safeString("USER_ID"),
            TITLE = safeString("TITLE"),
            TIMESTAMP_X = safeString("TIMESTAMP_X"),
            IMAGE_SIZE = getString("IMAGE_SIZE"),
            SRC = getString("SRC").parseImagePath()
        )

    //Promotion
    private fun JSONArray.parsePromotionEntityList(): List<PromotionEntity> =
        mutableListOf<PromotionEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parsePromotionEntity())
            }
        }

    private fun JSONObject.parsePromotionEntity() = PromotionEntity(
        id = getString("ID").toLong(),
        name = getString("NAME"),
        detailPicture = getString("PREVIEW_PICTURE").parseImagePath(),
        timeLeft = getString("DATAOUT"),
        statusColor = getString("CVET"),
        customerCategory = getString("NAMERAZDEL")
    )

    //SearchWord
    private fun JSONArray.parseSearchWordList(): List<String> = mutableListOf<String>().apply {
        for (index in 0 until length()) {
            add(getString(index))
        }
    }

    private fun JSONObject.parseReplacementProductsCategoryDetailEntity() = CategoryDetailEntity(
        id = 0,
        name = getString("glavtitle"),
        productEntityList = getJSONArray("data").parseProductEntityList()
    )

}