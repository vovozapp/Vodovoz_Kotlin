package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderCommentBinding
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.view_holder.CommentSliderViewHolder

class CommentsSliderAdapter() : RecyclerView.Adapter<CommentSliderViewHolder>() {

    var commentUIList = listOf<CommentUI>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommentSliderViewHolder(
        binding = ViewHolderSliderCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: CommentSliderViewHolder,
        position: Int
    ) = holder.onBind(commentUIList[position])

    override fun getItemCount() = commentUIList.size

}