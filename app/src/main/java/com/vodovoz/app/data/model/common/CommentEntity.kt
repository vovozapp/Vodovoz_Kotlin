package com.vodovoz.app.data.model.common

import com.vodovoz.app.data.parser.response.comment.AllCommentsByProductResponseJsonParser

class CommentEntity(
    val id: Long? = null,
    val text: String? = null,
    val date: String? = null,
    val author: String? = null,
    val authorPhoto: String? = null,
    val rating: Int? = null,
    val images: List<AllCommentsByProductResponseJsonParser.CommentImageEntity>? = null
)