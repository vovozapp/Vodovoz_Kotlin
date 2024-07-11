package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.AllCommentsEntity
import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.data.model.common.CommentsData
import com.vodovoz.app.feature.all.comments.model.CommentImage
import com.vodovoz.app.ui.model.AllCommentsUI
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.model.CommentsDataUI

object CommentMapper {

    fun AllCommentsEntity.mapToUI() = AllCommentsUI(
        comments = comments.mapToUI(),
        commentsData = commentsData.mapToUI()
    )

    fun CommentsData.mapToUI() = CommentsDataUI(
        rating = rating,
        commentCount = commentCount,
        commentCountText = commentCountText
    )

    fun List<CommentEntity>.mapToUI(): List<CommentUI> = mutableListOf<CommentUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun CommentEntity.mapToUI() = CommentUI(
        id = id,
        text = text,
        date = date,
        author = author,
        rating = rating,
        authorPhoto = authorPhoto,
        ratingComment = ratingComment,
        commentImages = images?.map {
            CommentImage(it.ID, it.SRC)
        }
    )

}
