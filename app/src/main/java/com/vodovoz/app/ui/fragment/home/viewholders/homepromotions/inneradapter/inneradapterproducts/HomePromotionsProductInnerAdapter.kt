package com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter.inneradapterproducts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter.HomePromotionsDiffUtilCallback
import com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter.HomePromotionsInnerViewHolder
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.PromotionUI

class HomePromotionsProductInnerAdapter(
    private val clickListener: HomePromotionsProductInnerClickListener
) : RecyclerView.Adapter<HomePromotionsProductInnerViewHolder>(){

    private val items = mutableListOf<ProductUI>()

    private val adapterListUpdateCallback = AdapterListUpdateCallback(this)


    fun submitList(items: List<ProductUI>) {
        val diff = DiffUtil.calculateDiff(HomePromotionsProductInnerDiffUtilCallback(this.items, items), false)
        diff.dispatchUpdatesTo(adapterListUpdateCallback)

        this.items.clear()
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePromotionsProductInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_promotion_product, parent, false)
        return HomePromotionsProductInnerViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomePromotionsProductInnerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getItem(position: Int) : ProductUI? {
        return items.getOrNull(position)
    }
}