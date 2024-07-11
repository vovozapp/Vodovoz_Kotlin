package com.vodovoz.app.ui.model

data class AllCommentsUI(
    val comments: List<CommentUI> = emptyList(),
    val commentsData: CommentsDataUI = CommentsDataUI()
)
