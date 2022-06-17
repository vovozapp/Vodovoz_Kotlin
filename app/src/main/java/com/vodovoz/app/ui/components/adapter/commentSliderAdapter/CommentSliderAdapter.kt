package com.vodovoz.app.ui.components.adapter.commentSliderAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderCommentBinding
import com.vodovoz.app.ui.model.CommentUI

class CommentSliderAdapter() : RecyclerView.Adapter<CommentSliderViewHolder>() {

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