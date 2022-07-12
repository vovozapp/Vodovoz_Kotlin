package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.ui.model.CommentUI

object CommentMapper {

    fun List<CommentEntity>.mapToUI(): List<CommentUI> = mutableListOf<CommentUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun CommentEntity.mapToUI() = CommentUI(
        id = id,
        text = text,
        date = date,
        author = author,
        rating = rating,
        authorPhoto = authorPhoto
    )

}
