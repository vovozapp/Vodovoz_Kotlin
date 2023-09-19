package com.vodovoz.app.feature.profile.waterapp.viewholder.inner

import android.view.View
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentWaterAppInnerFirstBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.model.inner.WaterAppModelInnerOne
import com.vodovoz.app.util.extensions.color
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.launch

class WaterAppViewHolderInnerFirst(
    view: View,
    private val clickListener: WaterAppClickListener,
    private val waterAppHelper: WaterAppHelper,
    private val innerClickListener: WaterAppInnerClickListener,
) : ItemViewHolder<WaterAppModelInnerOne>(view) {

    private val binding: FragmentWaterAppInnerFirstBinding = FragmentWaterAppInnerFirstBinding.bind(view)

    init {
        binding.llMan.setOnClickListener { waterAppHelper.saveGender("man") }
        binding.llWoman.setOnClickListener { waterAppHelper.saveGender("woman") }
    }

    override fun attach() {
        super.attach()
        val item = item ?: return
        innerClickListener.onChangePosition(item.id)
        launch {
            waterAppHelper
                .observeWaterAppUserData()
                .collect {
                    if (it == null) return@collect
                    debugLog { "inner ${it.gender}" }
                    if (it.gender == "man") {
                        selectMan()
                    } else {
                        selectWoman()
                    }
                }
        }
    }

    private fun selectMan() {
        binding.tvMan.setTextColor(itemView.context.color(R.color.bluePrimary))
        binding.tvWoman.setTextColor(itemView.context.color(R.color.gray_unselected))
        binding.manIv.alpha = 1f
        binding.womanIv.alpha = 0.5f
    }

    private fun selectWoman() {
        binding.tvWoman.setTextColor(itemView.context.color(R.color.bluePrimary))
        binding.tvMan.setTextColor(itemView.context.color(R.color.gray_unselected))
        binding.womanIv.alpha = 1f
        binding.manIv.alpha = 0.5f
    }
}