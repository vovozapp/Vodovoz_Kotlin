package com.vodovoz.app.feature.productdetail.viewholders.detailcomments

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentProductDetailsCommentsBinding
import com.vodovoz.app.feature.all.comments.adapter.CommentImagesAdapter
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productdetail.viewholders.detailcomments.inner.CommentsWithAvatarFlowAdapter
import com.vodovoz.app.ui.decoration.CommentMarginDecoration

class DetailCommentsViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener,
) : ItemViewHolder<DetailComments>(view) {

    private val binding: FragmentProductDetailsCommentsBinding =
        FragmentProductDetailsCommentsBinding.bind(view)
    private val adapter: CommentsWithAvatarFlowAdapter = CommentsWithAvatarFlowAdapter()
    private val imagesAdapter = CommentImagesAdapter()
    private val space: Int by lazy {
        itemView.context.resources.getDimension(R.dimen.space_16).toInt()
    }

    init {
        binding.rvImages.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.adapter = imagesAdapter

        binding.commentRecycler.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)

        binding.commentRecycler.adapter = adapter

        binding.commentRecycler.addItemDecoration(
            DividerItemDecoration(
                itemView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.commentRecycler.addItemDecoration(CommentMarginDecoration(space))

        binding.tvShowAllComment.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onShowAllComments(item.productId)
        }

        binding.tvSendComment.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onSendComment(item.productId)
        }
    }

    override fun getState(): Parcelable? {
        return binding.commentRecycler.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.commentRecycler.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: DetailComments) {
        super.bind(item)

        imagesAdapter.submitList(item.commentImages ?: emptyList())

        binding.tvCommentAmountTitle.text = StringBuilder()
            .append("Отзывы (")
            .append(item.commentUIList.size)
            .append(")")
            .toString()

        if (item.commentUIList.isEmpty()) {
            binding.llCommentsTitleContainer.visibility = View.GONE
            binding.noCommentsTitle.visibility = View.VISIBLE
        } else {
            binding.llCommentsTitleContainer.visibility = View.VISIBLE
            binding.noCommentsTitle.visibility = View.GONE
        }

        adapter.submitList(item.commentUIList)
    }

}