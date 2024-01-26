package com.vodovoz.app.data.parser.response.search

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object MatchesQueriesResponseJsonParser {

    fun ResponseBody.parseMatchesQueriesResponse(): ResponseEntity<List<String>> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                if(responseJson.has("data") && !responseJson.isNull("data")) {
                    responseJson.getJSONArray("data").parseQueryList()
                } else {
                    listOf()
                }
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseQueryList(): List<String> = mutableListOf<String>().also { list ->
        for (index in 0 until length()) {
            list.add(getString(index))
        }
    }
}