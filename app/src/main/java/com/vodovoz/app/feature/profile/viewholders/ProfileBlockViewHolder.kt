package com.vodovoz.app.feature.profile.viewholders

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemProfileBlockBinding
import com.vodovoz.app.feature.profile.viewholders.block.BlockAdapter
import com.vodovoz.app.feature.profile.viewholders.models.ProfileBlock

class ProfileBlockViewHolder(
    view: View
) : ItemViewHolder<ProfileBlock>(view) {

    private val binding: ItemProfileBlockBinding = ItemProfileBlockBinding.bind(view)
    private val space = itemView.resources.getDimension(R.dimen.space_16).toInt()
    private val blockAdapter = BlockAdapter()

    init {

        binding.vpOrders.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.vpOrders.adapter = blockAdapter

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

    override fun bind(item: ProfileBlock) {
        super.bind(item)

        blockAdapter.submitList(item.data)

    }
}