package com.vodovoz.app.data.parser.response.user

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object PersonalProductsJsonParser {

    fun ResponseBody.parsePersonalProductsResponse(): ResponseEntity<CategoryDetailEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                CategoryDetailEntity(
                    id = 0,
                    name = responseJson.getString("title"),
                    productEntityList = responseJson.getJSONArray("data").parseProductEntityList()
                )
            )
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

}