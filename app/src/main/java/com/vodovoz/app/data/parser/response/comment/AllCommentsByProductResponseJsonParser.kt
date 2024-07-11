package com.vodovoz.app.data.parser.response.comment

import com.vodovoz.app.data.model.common.AllCommentsEntity
import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.data.model.common.CommentsData
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object AllCommentsByProductResponseJsonParser {

    fun ResponseBody.parseAllCommentsByProductResponse(): ResponseEntity<AllCommentsEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                AllCommentsEntity(
                    comments = when (responseJson.isNull("data")) {
                        true -> listOf()
                        else -> responseJson.getJSONArray("data").parseCommentEntityList()
                    },
                    commentsData = when (responseJson.isNull("osnova")) {
                        true -> CommentsData()
                        else -> responseJson.getJSONObject("osnova").parseCommentsData()
                    },
                )
            )

            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    //Comment
    private fun JSONArray.parseCommentEntityList(): List<CommentEntity> =
        mutableListOf<CommentEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseCommentEntity())
            }
        }

    private fun JSONObject.parseCommentEntity() = CommentEntity(
        author = if (has("NAME")) getString("NAME") else null,
        text = getString("TEXT"),
        date = getString("DATA"),
        authorPhoto = if (has("USER_PHOTO")) getString("USER_PHOTO").parseImagePath() else null,
        images = when (has("IMAGES") && !isNull("IMAGES")) {
            false -> null
            true -> getJSONArray("IMAGES").parseCommentImageEntityList()
        },
        rating = safeInt("RATING"),
        ratingComment = safeString("KYPLEN")
    )

    data class CommentImageEntity(
        val ID: String? = null,
        val FILE_ID: String? = null,
        val POST_ID: String? = null,
        val BLOG_ID: String? = null,
        val USER_ID: String? = null,
        val TITLE: String? = null,
        val TIMESTAMP_X: String? = null,
        val IMAGE_SIZE: String? = null,
        val SRC: String? = null,
    )

    //Comment
    private fun JSONArray.parseCommentImageEntityList(): List<CommentImageEntity> =
        mutableListOf<CommentImageEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseCommentImageEntity())
            }
        }

    private fun JSONObject.parseCommentImageEntity() = CommentImageEntity(
        ID = safeString("ID"),
        FILE_ID = safeString("FILE_ID"),
        POST_ID = safeString("POST_ID"),
        BLOG_ID = safeString("BLOG_ID"),
        USER_ID = safeString("USER_ID"),
        TITLE = safeString("TITLE"),
        TIMESTAMP_X = safeString("TIMESTAMP_X"),
        IMAGE_SIZE = getString("IMAGE_SIZE"),
        SRC = getString("SRC").parseImagePath()
    )

    private fun JSONObject.parseCommentsData() = CommentsData(
        rating = safeString("RATING"),
        commentCount = safeInt("COMMENT_COUNT"),
        commentCountText = safeString("COMMENT_COUNT_TEXT")
    )
}
