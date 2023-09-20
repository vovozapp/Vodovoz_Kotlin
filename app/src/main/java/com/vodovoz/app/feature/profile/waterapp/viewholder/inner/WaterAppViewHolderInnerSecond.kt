package com.vodovoz.app.feature.profile.waterapp.viewholder.inner

import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppInnerSecondBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppLists
import com.vodovoz.app.feature.profile.waterapp.model.inner.WaterAppModelInnerTwo
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.adapter.WaterAppPickerAdapter
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.launch

class WaterAppViewHolderInnerSecond(
    view: View,
    private val waterAppHelper: WaterAppHelper,
) : ItemViewHolder<WaterAppModelInnerTwo>(view) {

    private val binding: FragmentWaterAppInnerSecondBinding =
        FragmentWaterAppInnerSecondBinding.bind(view)

    private val layoutManager =
        LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)

    private val pickerAdapter =
        WaterAppPickerAdapter(waterAppHelper).apply { submitList(WaterAppLists.listOfHeight) }

    override fun attach() {
        super.attach()

        launch {
            waterAppHelper
                .observeWaterAppUserData()
                .collect {
                    if (it == null) return@collect
                    debugLog { "inner height ${it.height}" }
                    smoothSnapToPosition(it.height.toInt() - 10)
                }
        }
    }

    init {
        binding.rvItems.layoutManager = layoutManager
        binding.rvItems.adapter = pickerAdapter
        binding.rvItems.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == newState) {
                        smoothSnapToPosition(layoutManager.findFirstVisibleItemPosition())
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
    }

    internal fun smoothSnapToPosition(position: Int) {
        val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(itemView.context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }

            override fun getHorizontalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = position
        layoutManager.startSmoothScroll(smoothScroller)
        waterAppHelper.saveHeight((position + 10).toString())
    }

}