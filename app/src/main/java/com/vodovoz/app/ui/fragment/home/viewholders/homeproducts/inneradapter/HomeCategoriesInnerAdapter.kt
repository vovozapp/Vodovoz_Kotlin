package com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.CategoryDetailUI

class HomeCategoriesInnerAdapter(
    private val clickListener: HomeCategoriesInnerClickListener
) : RecyclerView.Adapter<HomeCategoriesInnerViewHolder>(){

    private val items = mutableListOf<CategoryDetailUI>()

    private val adapterListUpdateCallback = AdapterListUpdateCallback(this)

    fun submitList(items: List<CategoryDetailUI>) {
        val diff = DiffUtil.calculateDiff(HomeCategoriesInnerDiffUtilCallback(this.items, items), false)
        diff.dispatchUpdatesTo(adapterListUpdateCallback)

        this.items.clear()
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoriesInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_product_category, parent, false)
        return HomeCategoriesInnerViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeCategoriesInnerViewHolder, position: Int) {
        holder.bind(items[position], position == items.size - 1)
    }
}