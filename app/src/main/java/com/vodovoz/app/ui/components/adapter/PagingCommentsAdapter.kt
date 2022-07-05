package com.vodovoz.app.ui.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderCommentWithAvatarBinding
import com.vodovoz.app.databinding.ViewHolderProductGridBinding
import com.vodovoz.app.databinding.ViewHolderProductListBinding
import com.vodovoz.app.ui.components.diffUtils.CommentDiffItemCallback
import com.vodovoz.app.ui.components.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.ui.components.view_holder.CommentWithAvatarViewHolder
import com.vodovoz.app.ui.components.view_holder.ProductGridViewHolder
import com.vodovoz.app.ui.components.view_holder.ProductListViewHolder
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PagingCommentsAdapter(
    private val commentDiffItemCallback: CommentDiffItemCallback
) : PagingDataAdapter<CommentUI, CommentWithAvatarViewHolder>(commentDiffItemCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CommentWithAvatarViewHolder(
        binding = ViewHolderCommentWithAvatarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: CommentWithAvatarViewHolder,
        position: Int
    ) = holder.onBind(getItem(position)!!)

    enum class ViewMode(val code: Int) {
        LIST(0), GRID(1)
    }

}