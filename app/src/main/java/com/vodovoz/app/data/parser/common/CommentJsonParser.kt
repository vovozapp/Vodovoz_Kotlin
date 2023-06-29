package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.CommentEntity
import org.json.JSONArray
import org.json.JSONObject

object CommentJsonParser {

    fun JSONArray.parseCommentEntityList(): List<CommentEntity> = mutableListOf<CommentEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseCommentEntity())
        }
    }

    fun JSONObject.parseCommentEntity() = CommentEntity(
        id = getLong("ID"),
        text = safeString("PREVIEW_TEXT"),
        date = safeString("DATE_ACTIVE_FROM"),
        author = safeString("PROPERTY_AUTHOR_VALUE"),
        rating = safeString("PROPERTY_RATING_VALUE_VALUE").parseRating()
    )

    private fun String.parseRating() = when(this) {
        "4" -> 4
        "5" -> 5
        "3" -> 3
        "2" -> 2
        "1" -> 1
        else -> 0
    }

}