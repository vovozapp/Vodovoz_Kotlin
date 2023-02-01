package com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter.inneradapterproducts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.ProductUI

class HomePromotionsProductInnerAdapter(
    private val clickListener: HomePromotionsProductInnerClickListener
) : ListAdapter<ProductUI, HomePromotionsProductInnerViewHolder>(HomePromotionsProductInnerDiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePromotionsProductInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_promotion_product, parent, false)
        return HomePromotionsProductInnerViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: HomePromotionsProductInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}