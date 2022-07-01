package com.vodovoz.app.ui.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderFilterValueBinding
import com.vodovoz.app.ui.components.view_holder.ProductFilterValueViewHolder
import com.vodovoz.app.ui.model.FilterValueUI

class ProductFilterValuesAdapter() : RecyclerView.Adapter<ProductFilterValueViewHolder>() {

    var filterValueList = listOf<FilterValueUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductFilterValueViewHolder(
        binding = ViewHolderFilterValueBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(
        holder: ProductFilterValueViewHolder,
        position: Int
    ) = holder.onBind(filterValueList[position])

    override fun getItemCount() = filterValueList.size

}