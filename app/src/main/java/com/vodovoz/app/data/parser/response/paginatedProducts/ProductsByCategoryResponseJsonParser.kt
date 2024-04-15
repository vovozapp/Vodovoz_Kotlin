package com.vodovoz.app.data.parser.response.paginatedProducts

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.FilterValueEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.SortTypeListJsonParser.parseSortTypeList
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ProductsByCategoryResponseJsonParser {

    fun ResponseBody.parseProductsByCategoryResponse(): ResponseEntity<CategoryEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> {
                val primaryFilterName = when (responseJson.has("brand")) {
                    true -> responseJson.getJSONObject("brand").getString("NAME")
                    else -> null
                }

                val primaryFilterValueList = if (responseJson.has("brand")) {
                    when (responseJson.getJSONObject("brand").isNull("POPYLBRAND")) {
                        false -> {
                            parseBrandFilterValueEntityList(
                                responseJson.getJSONObject("brand").getJSONArray("POPYLBRAND")
                            )
                        }

                        true -> listOf()
                    }
                } else listOf()

                val filterCode = when (responseJson.has("brand")) {
                    true -> responseJson.getJSONObject("brand").safeString("SIMVOL_CODE")
                    else -> ""
                }

                val productsList = when (responseJson.isNull("data")) {
                    true -> listOf()
                    false -> responseJson.getJSONArray("data").parseProductEntityList()
                }

                return ResponseEntity.Success(
                    CategoryEntity(
                        id = 0,
                        productAmount = responseJson.getString("tovarvsego"),
                        name = responseJson.getString("titlerazdel"),
                        primaryFilterName = primaryFilterName,
                        primaryFilterValueList = primaryFilterValueList,
                        shareUrl = when (responseJson.has("detail_page_url") && !responseJson.isNull(
                            "detail_page_url"
                        )) {
                            false -> ""
                            true -> responseJson.getString("detail_page_url")
                        },
                        sortTypeList = when (responseJson.has("sortirovka")) {
                            false -> null
                            true -> responseJson.getJSONObject("sortirovka").parseSortTypeList()
                        },
                        productList = productsList,
                        limit = responseJson.safeInt("limit"),
                        totalCount = responseJson.safeInt("count"),
                        title = responseJson.safeString("title"),
                        message = responseJson.safeString("message")
                    )
                )
            }

            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun parseBrandFilterValueEntityList(
        brandCategoryJSONArray: JSONArray,
    ): List<FilterValueEntity> = mutableListOf<FilterValueEntity>().apply {
        for (index in 0 until brandCategoryJSONArray.length()) {
            add(parseBrandFilterValueEntity(brandCategoryJSONArray.getJSONObject(index)))
        }
    }

    private fun parseBrandFilterValueEntity(
        brandCategoryJSON: JSONObject,
    ) = FilterValueEntity(
        id = brandCategoryJSON.getString("ID"),
        value = brandCategoryJSON.getString("NAME")
    )

}