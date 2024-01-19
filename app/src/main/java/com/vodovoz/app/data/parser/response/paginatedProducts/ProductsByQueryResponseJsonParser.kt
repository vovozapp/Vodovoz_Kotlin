package com.vodovoz.app.data.parser.response.paginatedProducts

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SearchQueryResponse
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object ProductsByQueryResponseJsonParser {

    fun ResponseBody.parseProductsByQueryResponse(): ResponseEntity<SearchQueryResponse> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.parseSearchQueryResponse()
            )
            else -> ResponseEntity.Success(SearchQueryResponse())
        }
    }

    private fun JSONObject.parseSearchQueryResponse() = if (isNull("data")) {
        SearchQueryResponse(
            deepLink = safeString("perexod"),
            id = safeString("id")
        )
    } else {
        SearchQueryResponse(
            productList = getJSONArray("data").parseProductEntityList()
        )
    }

}