package com.vodovoz.app.feature.home.viewholders.homeorders

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentSliderOrderBinding
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersSliderClickListener

class HomeOrdersSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener,
    repeatOrderClickListener: (Long) -> Unit,
) : ItemViewHolder<HomeOrders>(view) {

    private val binding: FragmentSliderOrderBinding = FragmentSliderOrderBinding.bind(view)
    private val space = itemView.resources.getDimension(R.dimen.space_16).toInt()
    private val homeOrdersAdapter =
        HomeOrdersInnerAdapter(getHomeOrdersSliderClickListener(), repeatOrderClickListener)

    init {

        binding.vpOrders.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.vpOrders.adapter = homeOrdersAdapter

        binding.vpOrders.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State,
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
    }


    override fun bind(item: HomeOrders) {
        super.bind(item)

        homeOrdersAdapter.submitList(item.items)

    }

    private fun getHomeOrdersSliderClickListener(): HomeOrdersSliderClickListener {
        return object : HomeOrdersSliderClickListener {
            override fun onOrderClick(id: Long?) {
                clickListener.onOrderClick(id)
            }
        }
    }

}