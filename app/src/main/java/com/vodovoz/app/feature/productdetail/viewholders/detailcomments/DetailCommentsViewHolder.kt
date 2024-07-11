package com.vodovoz.app.feature.productdetail.viewholders.detailcomments

import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentProductDetailsCommentsBinding
import com.vodovoz.app.feature.all.comments.adapter.CommentImagesAdapter
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productdetail.viewholders.detailcomments.inner.CommentsWithAvatarFlowAdapter

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
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        binding.commentRecycler.adapter = adapter

        binding.commentRecycler.addItemDecoration(object : ItemDecoration() {

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State,
            ) {
                val space = binding.root.context.resources.getDimension(R.dimen.space_8).toInt()
                outRect.top = space
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.left = space / 2
                }
                if (parent.getChildAdapterPosition(view) != adapter.itemCount - 1) {
                    outRect.right = space / 2
                }
            }
        })
        binding.commentRecycler.setHasFixedSize(true)
        binding.commentRecycler.visibility = View.INVISIBLE

        binding.llRatingContainer.setOnClickListener {
            val item = item ?: return@setOnClickListener
            if (item.rating != "0") {
                clickListener.onShowAllComments(item.productId)
            }
        }

        binding.writeCommentCardView.setOnClickListener {
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

        setIsRecyclable(false)
        imagesAdapter.submitList(item.commentImages ?: emptyList())

        adapter.submitList(item.commentUIList)

        binding.commentRecycler.smoothScrollToPosition(item.commentUIList.size)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.commentRecycler.smoothScrollToPosition(0)
        }, 100)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.commentRecycler.visibility = View.VISIBLE
        }, 200)

        binding.ratingTextBottom.text = item.rating

        if (item.rating == "0") {
            binding.counterCommentsBottom.visibility = View.GONE
            binding.counterCommentsBottomArrow.visibility = View.GONE
        } else {
            binding.counterCommentsBottom.visibility = View.VISIBLE
            binding.counterCommentsBottomArrow.visibility = View.VISIBLE
        }

        binding.counterCommentsBottom.text = item.commentsAmountText
    }
}