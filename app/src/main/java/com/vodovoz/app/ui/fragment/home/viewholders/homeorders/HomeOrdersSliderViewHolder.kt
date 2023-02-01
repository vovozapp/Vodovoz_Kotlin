package com.vodovoz.app.ui.fragment.home.viewholders.homeorders

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderOrderBinding
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.fragment.home.viewholders.homeorders.inneradapter.HomeOrdersInnerAdapter
import com.vodovoz.app.ui.fragment.home.viewholders.homeorders.inneradapter.HomeOrdersSliderClickListener

class HomeOrdersSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSliderOrderBinding = FragmentSliderOrderBinding.bind(view)

    fun bind(items: HomeOrders) {
        initOrderPager(items)
    }

    private fun initOrderPager(items: HomeOrders) {
        val space = itemView.resources.getDimension(R.dimen.space_16).toInt()
        binding.vpOrders.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpOrders.adapter = HomeOrdersInnerAdapter(getHomeOrdersSliderClickListener()).apply {
            submitList(items.items)
        }
        binding.vpOrders.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        left = space
                        top = space / 2
                        right = space
                        bottom = space / 2
                    }
                }
            }
        )

        when(items.orderSliderConfig.containTitleContainer) {
            true -> binding.llTitleContainer.visibility = View.VISIBLE
            false -> binding.llTitleContainer.visibility = View.GONE
        }

        binding.tvShowAll.setOnClickListener { clickListener.onShowAllOrdersClick() }
    }

    private fun getHomeOrdersSliderClickListener() : HomeOrdersSliderClickListener {
        return object : HomeOrdersSliderClickListener {
            override fun onOrderClick(id: Long?) {
                clickListener.onOrderClick(id)
            }
        }
    }

}