package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.CommentEntity
import org.json.JSONArray
import org.json.JSONObject

class CommentJsonParser {

    fun parseCommentEntityList(
        commentJSONArray: JSONArray
    ): List<CommentEntity> = mutableListOf<CommentEntity>().apply {
        for (index in 0 until commentJSONArray.length()) {
            add(parseCommentEntity(commentJSONArray.getJSONObject(index)))
        }
    }

    fun parseCommentEntity(
        commentJson: JSONObject
    ) = CommentEntity(
        id = commentJson.getLong("ID"),
        text = commentJson.getString("PREVIEW_TEXT"),
        date = commentJson.getString("DATE_ACTIVE_FROM"),
        author = commentJson.getString("PROPERTY_AUTHOR_VALUE"),
        rating = parseRating(commentJson.getString("PROPERTY_RATING_VALUE_VALUE"))
    )

    private fun parseRating(ratingStr: String) = when(ratingStr) {
        "хороший магазин" -> 4
        "отличный магазин" -> 5
        "обычный магазин" -> 3
        else -> throw Exception()
    }

}