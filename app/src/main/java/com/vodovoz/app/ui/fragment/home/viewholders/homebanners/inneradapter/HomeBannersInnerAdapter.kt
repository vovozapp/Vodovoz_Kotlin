package com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter.HomeCategoriesInnerDiffUtilCallback
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI

class HomeBannersInnerAdapter(
    private val clickListener: HomeBannersSliderClickListener
) : RecyclerView.Adapter<HomeBannersInnerViewHolder>(){

    private val items = mutableListOf<BannerUI>()

    private val adapterListUpdateCallback = AdapterListUpdateCallback(this)


    fun submitList(items: List<BannerUI>) {
        val diff = DiffUtil.calculateDiff(HomeBannersInnerDiffUtilCallback(this.items, items), false)
        diff.dispatchUpdatesTo(adapterListUpdateCallback)

        this.items.clear()
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannersInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_banner, parent, false)
        return HomeBannersInnerViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeBannersInnerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getItem(position: Int) : BannerUI? {
        return items.getOrNull(position)
    }
}