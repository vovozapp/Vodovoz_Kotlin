package com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter.HomeBannersInnerDiffUtilCallback
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter.HomeBannersInnerViewHolder
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.PromotionUI

class HomePromotionsInnerAdapter(
    private val clickListener: HomePromotionsSliderClickListener
) : RecyclerView.Adapter<HomePromotionsInnerViewHolder>(){

    private val items = mutableListOf<PromotionUI>()

    private val adapterListUpdateCallback = AdapterListUpdateCallback(this)


    fun submitList(items: List<PromotionUI>) {
        val diff = DiffUtil.calculateDiff(HomePromotionsDiffUtilCallback(this.items, items), false)
        diff.dispatchUpdatesTo(adapterListUpdateCallback)

        this.items.clear()
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePromotionsInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_promotion, parent, false)
        return HomePromotionsInnerViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomePromotionsInnerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getItem(position: Int) : PromotionUI? {
        return items.getOrNull(position)
    }
}