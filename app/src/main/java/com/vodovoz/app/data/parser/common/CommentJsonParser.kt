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
        text = getString("PREVIEW_TEXT"),
        date = getString("DATE_ACTIVE_FROM"),
        author = getString("PROPERTY_AUTHOR_VALUE"),
        rating = getString("PROPERTY_RATING_VALUE_VALUE").parseRating()
    )

    private fun String.parseRating() = when(this) {
        "хороший магазин" -> 4
        "отличный магазин" -> 5
        "обычный магазин" -> 3
        "плохой магазин" -> 2
        "ужасный магазин" -> 1
        else -> throw Exception()
    }

}