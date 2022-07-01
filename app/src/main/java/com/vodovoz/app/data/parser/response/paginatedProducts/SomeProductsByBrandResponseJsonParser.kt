package com.vodovoz.app.data.parser.response.paginatedProducts

import com.vodovoz.app.data.model.common.PaginatedProductListEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object SomeProductsByBrandResponseJsonParser {

    fun ResponseBody.parseSomeProductsByBrandResponse(): ResponseEntity<PaginatedProductListEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                PaginatedProductListEntity(
                    pageAmount = responseJson.getInt("stranic"),
                    productEntityList = responseJson.getJSONArray("data").parseProductEntityList()
                )
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

}