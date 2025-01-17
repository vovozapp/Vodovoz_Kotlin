package com.vodovoz.app.feature.all

import android.view.ViewGroup
import com.vodovoz.app.R

import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressViewHolder
import com.vodovoz.app.feature.all.brands.AllBrandsViewHolder
import com.vodovoz.app.feature.all.orders.AllOrdersViewHolder
import com.vodovoz.app.feature.all.promotions.AllPromotionsViewHolder
import com.vodovoz.app.ui.model.BrandUI
import com.vodovoz.app.ui.model.OrderUI.Companion.ORDER_VIEW_TYPE
import com.vodovoz.app.ui.model.PromotionUI

class AllAdapter(
    private val allClickListener: AllClickListener,
): ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            PromotionUI.PROMOTION_UI_VIEW_TYPE -> {
                AllPromotionsViewHolder(getViewFromInflater(R.layout.view_holder_promotion, parent), allClickListener)
            }
            BrandUI.BRAND_UI_VIEW_TYPE -> {
                AllBrandsViewHolder(getViewFromInflater(R.layout.view_holder_brand_with_name, parent), allClickListener)
            }
            ORDER_VIEW_TYPE -> {
                AllOrdersViewHolder(getViewFromInflater( R.layout.view_holder_order_details, parent), allClickListener)
            }
            R.layout.item_progress -> {
                BottomProgressViewHolder(getViewFromInflater(viewType, parent))
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}