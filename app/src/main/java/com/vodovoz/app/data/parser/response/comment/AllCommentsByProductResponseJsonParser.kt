package com.vodovoz.app.data.parser.response.comment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object AllCommentsByProductResponseJsonParser {

    fun ResponseBody.parseAllCommentsByProductResponse(): ResponseEntity<List<CommentEntity>> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                when (responseJson.isNull("data")) {
                    true -> listOf()
                    else -> responseJson.getJSONArray("data").parseCommentEntityList()
                }
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
        author = getString("NAME"),
        text = getString("TEXT"),
        date = getString("DATA"),
        authorPhoto = getString("USER_PHOTO").parseImagePath(),
        images = when(has("IMAGES") && !isNull("IMAGES") ) {
            false -> listOf()
            true -> getJSONArray("IMAGES").parseCommentImageEntityList()
        }
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

}