package com.vodovoz.app.ui.fragment.home.viewholders.homecomments

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentSliderBannerBinding
import com.vodovoz.app.databinding.FragmentSliderCommentBinding
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.CommentUI

class HomeCommentsSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSliderCommentBinding = FragmentSliderCommentBinding.bind(view)

    init {

    }

    fun bind(items: HomeComments) {

    }

}