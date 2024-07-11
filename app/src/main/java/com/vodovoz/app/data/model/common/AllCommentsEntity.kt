package com.vodovoz.app.data.model.common

class AllCommentsEntity(
    val comments: List<CommentEntity> = listOf(),
    val commentsData: CommentsData = CommentsData()
)
