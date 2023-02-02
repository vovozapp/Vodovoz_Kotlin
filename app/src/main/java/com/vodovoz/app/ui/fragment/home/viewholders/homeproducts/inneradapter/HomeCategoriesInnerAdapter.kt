package com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.CategoryDetailUI

class HomeCategoriesInnerAdapter(
    private val clickListener: HomeCategoriesInnerClickListener
) : ListAdapter<CategoryDetailUI, HomeCategoriesInnerViewHolder>(HomeCategoriesInnerDiffUtilCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoriesInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_product_category, parent, false)
        return HomeCategoriesInnerViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: HomeCategoriesInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}