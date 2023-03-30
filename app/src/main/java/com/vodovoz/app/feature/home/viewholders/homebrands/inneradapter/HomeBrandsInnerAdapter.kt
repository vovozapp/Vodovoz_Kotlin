package com.vodovoz.app.feature.home.viewholders.homebrands.inneradapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.BrandUI.Companion.BRAND_UI_VIEW_TYPE

class HomeBrandsInnerAdapter(
    private val clickListener: HomeBrandsSliderClickListener
) : ItemAdapter(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {

        return when(viewType) {
            BRAND_UI_VIEW_TYPE-> {
                HomeBrandsInnerViewHolder(getViewFromInflater(R.layout.view_holder_slider_brand, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}