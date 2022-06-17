package com.vodovoz.app.data.parser.response

import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.CommentJsonParser
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

class CommentResponseJsonParser(
    private val commentJsonParser: CommentJsonParser
) {

    fun parseResponse(
        responseBody: ResponseBody
    ): ResponseEntity<List<CommentEntity>> {
        val responseJson = JSONObject(responseBody.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                commentJsonParser.parseCommentEntityList(responseJson.getJSONArray("data")))
            else -> ResponseEntity.Error()
        }
    }

}