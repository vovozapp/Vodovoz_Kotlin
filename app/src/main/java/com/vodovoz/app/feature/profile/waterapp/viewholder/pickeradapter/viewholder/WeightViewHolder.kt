package com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemPickerBinding
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppClickListener
import com.vodovoz.app.feature.profile.waterapp.adapter.WaterAppInnerClickListener
import com.vodovoz.app.feature.profile.waterapp.viewholder.pickeradapter.model.PickerWeight
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.launch

class WeightViewHolder(
    view: View,
    clickListener: WaterAppClickListener,
    private val waterAppHelper: WaterAppHelper,
    private val innerClickListener: WaterAppInnerClickListener,
) : ItemViewHolder<PickerWeight>(view) {

    private val binding: ItemPickerBinding = ItemPickerBinding.bind(view)

    init {

    }

    override fun attach() {
        super.attach()

        launch {
            waterAppHelper
                .observeWaterAppUserData()
                .collect {
                    if (it == null || it.weight.isEmpty()) return@collect
                    debugLog { "weight ${it.weight} id ${item?.id}" }

                    if (item?.id == it.weight.toInt()) {
                        binding.tvValue.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.bluePrimary
                            )
                        )
                    } else {
                        binding.tvValue.setTextColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.gray_unselected
                            )
                        )
                    }
                }
        }
    }

    override fun bind(item: PickerWeight) {
        super.bind(item)

        binding.tvValue.text = buildString {
            append(item.id)
            append(" кг")
        }
    }
}