package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.feature.all.comments.model.CommentImage
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.util.extensions.fromHtml

object CommentMapper {

    fun List<CommentEntity>.mapToUI(): List<CommentUI> = mutableListOf<CommentUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun CommentEntity.mapToUI() = CommentUI(
        id = id,
        text = text?.fromHtml().toString(),
        date = date,
        author = author,
        rating = rating,
        authorPhoto = authorPhoto,
        commentImages = images?.map {
            CommentImage(it.ID, it.SRC)
        }
    )

}
