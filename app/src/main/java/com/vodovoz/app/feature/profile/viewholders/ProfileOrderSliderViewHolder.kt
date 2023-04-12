package com.vodovoz.app.feature.profile.viewholders

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemProfileOrderSliderBinding
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersSliderClickListener
import com.vodovoz.app.feature.profile.viewholders.models.ProfileOrders

class ProfileOrderSliderViewHolder(
    view: View,
    clickListener: HomeOrdersSliderClickListener
) : ItemViewHolder<ProfileOrders>(view) {

    private val binding: ItemProfileOrderSliderBinding = ItemProfileOrderSliderBinding.bind(view)

    private val space = itemView.resources.getDimension(R.dimen.space_16).toInt()
    private val homeOrdersAdapter = HomeOrdersInnerAdapter(clickListener)

    init {
        binding.vpOrders.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.vpOrders.adapter = homeOrdersAdapter

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
    }

    override fun bind(item: ProfileOrders) {
        super.bind(item)

        homeOrdersAdapter.submitList(item.data)
    }

}