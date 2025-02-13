package com.vodovoz.app.data.parser.response.comment

import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.CommentJsonParser.parseCommentEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import com.vodovoz.app.util.extensions.debugLog
import okhttp3.ResponseBody
import org.json.JSONObject

object CommentsSliderResponseJsonParser {

    fun ResponseBody.parseCommentsSliderResponse(): ResponseEntity<List<CommentEntity>> {
        val responseJson = JSONObject(string())
        debugLog { LogSettings.RESPONSE_BODY_LOG + " ${responseJson.toString(2)}" }
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").parseCommentEntityList()
            )
            else -> ResponseEntity.Hide()
        }
    }

}