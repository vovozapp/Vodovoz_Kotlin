package com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.inneradapter.inneradapterproducts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.ProductUI

class HomeProductsInnerAdapter(
    private val clickListener: HomeProductsInnerClickListener
) : ListAdapter<ProductUI, HomeProductsInnerViewHolder>(
    HomeProductsInnerDiffUtilCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeProductsInnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_slider_product, parent, false)
        return HomeProductsInnerViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: HomeProductsInnerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}