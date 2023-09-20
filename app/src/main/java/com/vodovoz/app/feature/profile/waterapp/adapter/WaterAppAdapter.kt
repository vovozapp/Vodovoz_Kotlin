package com.vodovoz.app.feature.profile.waterapp.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.profile.waterapp.WaterAppHelper
import com.vodovoz.app.feature.profile.waterapp.viewholder.*
import com.vodovoz.app.feature.profile.waterapp.viewholder.inner.*

class WaterAppAdapter(
    private val waterAppHelper: WaterAppHelper,
    private val clickListener: WaterAppClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.fragment_water_app_first -> { WaterAppViewHolderFirst(getViewFromInflater(viewType, parent), clickListener, waterAppHelper) }
            R.layout.fragment_water_app_second -> { WaterAppViewHolderSecond(getViewFromInflater(viewType, parent), clickListener, waterAppHelper) }
            R.layout.fragment_water_app_third -> { WaterAppViewHolderThird(getViewFromInflater(viewType, parent), clickListener, waterAppHelper) }
            R.layout.fragment_water_app_fourth -> { WaterAppViewHolderFourth(getViewFromInflater(viewType, parent), clickListener, waterAppHelper) }
            R.layout.fragment_water_app_fifth -> { WaterAppViewHolderFifth(getViewFromInflater(viewType, parent), clickListener, waterAppHelper) }
            R.layout.fragment_water_app_sixth -> { WaterAppViewHolderSixth(getViewFromInflater(viewType, parent), clickListener, waterAppHelper) }
            R.layout.fragment_water_app_seventh -> { WaterAppViewHolderSeventh(getViewFromInflater(viewType, parent), clickListener, waterAppHelper) }

            R.layout.fragment_water_app_inner_first -> { WaterAppViewHolderInnerFirst(getViewFromInflater(viewType, parent), waterAppHelper) }
            R.layout.fragment_water_app_inner_second -> { WaterAppViewHolderInnerSecond(getViewFromInflater(viewType, parent), waterAppHelper) }
            R.layout.fragment_water_app_inner_third -> { WaterAppViewHolderInnerThird(getViewFromInflater(viewType, parent),  waterAppHelper) }
            R.layout.fragment_water_app_inner_fourth -> { WaterAppViewHolderInnerFourth(getViewFromInflater(viewType, parent), waterAppHelper) }
            R.layout.fragment_water_app_inner_fifth -> { WaterAppViewHolderInnerFifth(getViewFromInflater(viewType, parent), waterAppHelper) }
            R.layout.fragment_water_app_inner_sixth -> { WaterAppViewHolderInnerSixth(getViewFromInflater(viewType, parent), waterAppHelper) }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}