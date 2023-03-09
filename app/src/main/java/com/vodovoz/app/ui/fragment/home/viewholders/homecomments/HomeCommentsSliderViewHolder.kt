package com.vodovoz.app.ui.fragment.home.viewholders.homecomments

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderCommentBinding
import com.vodovoz.app.ui.base.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.fragment.home.viewholders.homecomments.inneradapter.HomeCommentsInnerAdapter
import com.vodovoz.app.ui.fragment.home.viewholders.homecomments.inneradapter.HomeCommentsSliderClickListener

class HomeCommentsSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : ItemViewHolder<HomeComments>(view) {

    private val binding: FragmentSliderCommentBinding = FragmentSliderCommentBinding.bind(view)
    private val space = itemView.resources.getDimension(R.dimen.space_16).toInt()
    private val homeCommentsInnerAdapter = HomeCommentsInnerAdapter(getHomeCommentsSliderClickListener())

    init {
        binding.tvSendComment.setOnClickListener { clickListener.onSendCommentAboutShop() }

        binding.vpComments.addMarginDecoration {  rect, view, parent, state ->
            rect.left = space
            rect.right = space
            rect.bottom = space/2
            rect.top = space/2
        }

        binding.vpComments.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.vpComments.adapter = homeCommentsInnerAdapter
    }

    override fun bind(item: HomeComments) {
        super.bind(item)
        homeCommentsInnerAdapter.submitList(item.items)
    }

    private fun getHomeCommentsSliderClickListener() : HomeCommentsSliderClickListener {
        return object : HomeCommentsSliderClickListener {
            override fun onCommentClick(id: Long?) {
                clickListener.onCommentsClick(id)
            }
        }
    }

}