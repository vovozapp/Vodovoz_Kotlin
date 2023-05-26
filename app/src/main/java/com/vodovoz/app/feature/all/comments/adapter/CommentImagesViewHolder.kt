package com.vodovoz.app.feature.all.comments.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemCommentImageBinding
import com.vodovoz.app.feature.all.comments.model.CommentImage

class CommentImagesViewHolder(
    view: View,
    clickListener: CommentImagesClickListener,
) : ItemViewHolder<CommentImage>(view) {

    private val binding: ItemCommentImageBinding = ItemCommentImageBinding.bind(view)

    override fun bind(item: CommentImage) {
        super.bind(item)

        Glide.with(itemView.context)
            .load(item.url)
            .into(binding.imageComment)
    }
}