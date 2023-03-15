package com.vodovoz.app.feature.home.viewholders.homehistories

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderHistoryBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homehistories.inneradapter.HomeHistoriesInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homehistories.inneradapter.HomeHistoriesSliderClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class HomeHistoriesSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : ItemViewHolder<HomeHistories>(view) {

    private val binding: FragmentSliderHistoryBinding = FragmentSliderHistoryBinding.bind(view)
    private val historiesSliderAdapter = HomeHistoriesInnerAdapter(getHomeHistoriesSliderClickListener())

    init {
        binding.rvHistories.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        val space = itemView.resources.getDimension(R.dimen.space_16).toInt()

        binding.rvHistories.adapter = historiesSliderAdapter

        binding.rvHistories.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.left = space
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.right = space
            else rect.right = space/2
            rect.top = space / 2
            rect.bottom = space / 2
        }
    }

    override fun bind(item: HomeHistories) {
        super.bind(item)
        historiesSliderAdapter.submitList(item.items)
    }

    private fun getHomeHistoriesSliderClickListener() : HomeHistoriesSliderClickListener {
        return object : HomeHistoriesSliderClickListener {
            override fun onHistoryClick(id: Long) {
                clickListener.onHistoryClick(id)
            }
        }
    }

}