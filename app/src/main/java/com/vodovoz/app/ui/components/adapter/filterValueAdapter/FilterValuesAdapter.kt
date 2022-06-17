package com.vodovoz.app.ui.components.adapter.filterValueAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderFilterValueBinding
import com.vodovoz.app.ui.model.FilterValueUI

class FilterValuesAdapter() : RecyclerView.Adapter<FilterValueViewHolder>() {

    var filterValueList = listOf<FilterValueUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = FilterValueViewHolder(
        binding = ViewHolderFilterValueBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(
        holder: FilterValueViewHolder,
        position: Int
    ) = holder.onBind(filterValueList[position])

    override fun getItemCount() = filterValueList.size

}