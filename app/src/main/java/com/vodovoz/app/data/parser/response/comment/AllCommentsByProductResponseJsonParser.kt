package com.vodovoz.app.data.parser.response.comment

import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.CommentJsonParser.parseCommentEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object AllCommentsByProductResponseJsonParser {

    fun ResponseBody.parseAllCommentsByProductResponse(): ResponseEntity<List<CommentEntity>> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                when(responseJson.isNull("data")) {
                    true -> listOf()
                    else -> responseJson.getJSONArray("data").parseCommentEntityList()
                }
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    //Comment
    private fun JSONArray.parseCommentEntityList(): List<CommentEntity> = mutableListOf<CommentEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseCommentEntity())
        }
    }

    private fun JSONObject.parseCommentEntity() = CommentEntity(
        author = getString("NAME"),
        text = getString("TEXT"),
        date = getString("DATA"),
        authorPhoto = getString("USER_PHOTO").parseImagePath()
    )

}