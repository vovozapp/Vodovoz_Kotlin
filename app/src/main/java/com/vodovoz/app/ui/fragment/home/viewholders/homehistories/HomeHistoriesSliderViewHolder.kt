package com.vodovoz.app.ui.fragment.home.viewholders.homehistories

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentSliderBannerBinding
import com.vodovoz.app.databinding.FragmentSliderHistoryBinding
import com.vodovoz.app.ui.adapter.HistoriesSliderAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter.HomeBannersSliderClickListener
import com.vodovoz.app.ui.fragment.home.viewholders.homehistories.inneradapter.HomeHistoriesInnerAdapter
import com.vodovoz.app.ui.fragment.home.viewholders.homehistories.inneradapter.HomeHistoriesSliderClickListener
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.HistoryUI
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

class HomeHistoriesSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

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

    fun bind(items: HomeHistories) {
        historiesSliderAdapter.submitList(items.items)
    }

    private fun getHomeHistoriesSliderClickListener() : HomeHistoriesSliderClickListener {
        return object : HomeHistoriesSliderClickListener {
            override fun onHistoryClick(id: Long) {
                clickListener.onHistoryClick(id)
            }
        }
    }

}