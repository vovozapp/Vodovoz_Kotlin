package com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter.inneradapterproducts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter.HomeCategoriesInnerDiffUtilCallback
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter.HomeCategoriesInnerViewHolder
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI

class HomeProductsInnerAdapter(
    private val clickListener: HomeProductsInnerClickListener
) : RecyclerView.Adapter<HomeProductsInnerViewHolder>() {

    private val items = mutableListOf<ProductUI>()

    private val adapterListUpdateCallback = AdapterListUpdateCallback(this)

    fun submitList(items: List<ProductUI>) {
        val diff = DiffUtil.calculateDiff(HomeProductsInnerDiffUtilCallback(this.items, items), false)
        diff.dispatchUpdatesTo(adapterListUpdateCallback)

        this.items.clear()
        this.items.addAll(items)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeProductsInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_product, parent, false)
        return HomeProductsInnerViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeProductsInnerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getItem(position: Int) : ProductUI? {
        return items.getOrNull(position)
    }
}