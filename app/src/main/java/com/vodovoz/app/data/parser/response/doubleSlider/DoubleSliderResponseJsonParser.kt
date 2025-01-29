package com.vodovoz.app.data.parser.response.doubleSlider

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ParentSectionDataEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SectionDataEntity
import com.vodovoz.app.data.model.common.SectionsEntity
import com.vodovoz.app.data.model.features.SuperTopBundleEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject


object DoubleSliderResponseJsonParser {

    fun ResponseBody.parseBottomSliderResponse(): ResponseEntity<List<CategoryDetailEntity>> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONObject("RAZDEL_NIZ")
                    .getJSONArray("RAZDELY_NIZ").parseCategoryDetailEntityList()
            )

            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    fun ResponseBody.parseTopSliderResponse(): ResponseEntity<SuperTopBundleEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                if (responseJson.has("RAZDEL_VERH")
                    && !responseJson.isNull("RAZDEL_VERH")
                ) {
                    val item = responseJson.getJSONObject("RAZDEL_VERH")
                    SuperTopBundleEntity(
                        categoryDetailsList =
                        if (item.has("RAZDELY_VERH") && !item.isNull("RAZDELY_VERH")) {
                            item.getJSONArray("RAZDELY_VERH").parseCategoryDetailEntityList()
                        } else {
                            null
                        },
                        sectionsEntity = if (item.has("RAZDELY_VERH_NEW") && !item.isNull("RAZDELY_VERH_NEW")) {
                            val childItem = item.getJSONObject("RAZDELY_VERH_NEW")
                            if (childItem.has("DATA") && !childItem.isNull("DATA")) {
                                childItem.parseSectionsEntity()
                            } else {
                                null
                            }
                        } else {
                            null
                        },
                    )
                } else {
                    SuperTopBundleEntity(null, null)
                }
            )

            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseCategoryDetailEntityList(): List<CategoryDetailEntity> =
        mutableListOf<CategoryDetailEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseCategoryDetailEntity())
            }
        }

    private fun JSONObject.parseCategoryDetailEntity(): CategoryDetailEntity {
        val productEntityList = getJSONArray("data").parseProductEntityList()
        return CategoryDetailEntity(
            id = getLong("ID"),
            name = getString("NAME"),
            productAmount = productEntityList.size,
            productEntityList = productEntityList
        )
    }

    private fun JSONObject.parseSectionsEntity(): SectionsEntity {
        return SectionsEntity(
            color = if (has("COLORFON") && !isNull("COLORFON")) {
                getJSONObject("COLORFON").getString("ID")
            } else {
                null
            },
            parentSectionDataEntityList = getJSONArray("DATA").parseParentSectionsEntityList()
        )
    }

    private fun JSONArray.parseParentSectionsEntityList(): List<ParentSectionDataEntity> =
        mutableListOf<ParentSectionDataEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseParentSectionDataEntity())

            }
        }

    private fun JSONObject.parseParentSectionDataEntity() = ParentSectionDataEntity(
        title = safeString("NAME"),
        sectionDataEntityList = getJSONArray("data").parseSectionDataEntityList()
    )

    private fun JSONArray.parseSectionDataEntityList(): List<SectionDataEntity> =
        mutableListOf<SectionDataEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseSectionDataEntity())

            }
        }
    private fun JSONObject.parseSectionDataEntity() = SectionDataEntity (
        id = safeInt("IDRAZDEL"),
        name = safeString("NAME"),
        imageUrl = safeString("DETAIL_PICTURE"),
        promotionId = safeInt("IDAKCII"),
        cookieLink = safeString("SSILKAKYKI"),
    )

}



