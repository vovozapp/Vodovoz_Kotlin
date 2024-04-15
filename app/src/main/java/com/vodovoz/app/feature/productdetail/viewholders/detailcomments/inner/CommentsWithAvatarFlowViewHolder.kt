package com.vodovoz.app.feature.productdetail.viewholders.detailcomments.inner

import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderCommentWithAvatarBinding
import com.vodovoz.app.feature.all.comments.adapter.CommentImagesAdapter
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.util.extensions.fromHtml

class CommentsWithAvatarFlowViewHolder(
    view: View,
) : ItemViewHolder<CommentUI>(view) {

    private val binding: ViewHolderCommentWithAvatarBinding =
        ViewHolderCommentWithAvatarBinding.bind(view)

    private val imagesAdapter = CommentImagesAdapter()

    init {
        binding.rvImages.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.adapter = imagesAdapter
    }

    override fun getState(): Parcelable? {
        return binding.rvImages.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.rvImages.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: CommentUI) {
        super.bind(item)

        binding.date.text = item.date
        binding.tvAuthor.text = item.author
        binding.tvComment.text = item.text?.fromHtml()

        imagesAdapter.submitList(item.commentImages ?: emptyList())

        Glide.with(itemView.context)
            .load(item.authorPhoto)
            .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.png_default_avatar))
            .into(binding.imgAuthorPhoto)
    }
}