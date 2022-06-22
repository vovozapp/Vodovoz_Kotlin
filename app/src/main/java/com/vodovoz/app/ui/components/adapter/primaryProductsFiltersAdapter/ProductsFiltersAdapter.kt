package com.vodovoz.app.ui.components.adapter.primaryProductsFiltersAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderBrandFilterValueBinding
import com.vodovoz.app.ui.model.FilterValueUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductsFiltersAdapter(
    private val onFilterClickSubject: PublishSubject<FilterValueUI>
) : RecyclerView.Adapter<ProductsFilterViewHolder>() {

    var brandFilterValueList = listOf<FilterValueUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductsFilterViewHolder(
        binding = ViewHolderBrandFilterValueBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onFilterClickSubject = onFilterClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: ProductsFilterViewHolder,
        position: Int
    ) = holder.onBind(
        brandFilterValueList[position])

    override fun getItemCount() = brandFilterValueList.size

}