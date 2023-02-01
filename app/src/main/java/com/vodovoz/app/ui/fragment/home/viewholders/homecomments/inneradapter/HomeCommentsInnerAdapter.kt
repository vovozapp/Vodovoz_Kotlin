package com.vodovoz.app.ui.fragment.home.viewholders.homecomments.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.CommentUI

class HomeCommentsInnerAdapter(
    private val clickListener: HomeCommentsSliderClickListener
) : ListAdapter<CommentUI, HomeCommentsInnerViewHolder>(HomeCommentsDiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCommentsInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_comment, parent, false)
        return HomeCommentsInnerViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: HomeCommentsInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}