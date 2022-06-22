package com.vodovoz.app.data.parser.response.comment

import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.CommentJsonParser
import com.vodovoz.app.data.parser.common.CommentJsonParser.parseCommentEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object CommentsSliderResponseJsonParser {

    fun ResponseBody.parseCommentsSliderResponse(): ResponseEntity<List<CommentEntity>> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").parseCommentEntityList()
            )
            else -> ResponseEntity.Error()
        }
    }

}