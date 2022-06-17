package com.vodovoz.app.ui.mapper

import android.util.Log
import com.vodovoz.app.data.model.common.CommentEntity
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.util.LogSettings

object CommentMapper {

    fun List<CommentEntity>.mapToUI(): List<CommentUI> = mutableListOf<CommentUI>().also { uiList ->
        Log.i(LogSettings.ID_LOG, "PRE COM SIZE ${size}")
        forEach { uiList.add(it.mapToUI()) }
        Log.i(LogSettings.ID_LOG, "AFTER COM SIZE ${uiList.size}")
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
