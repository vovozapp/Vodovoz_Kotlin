package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderFilterBinding
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.view_holder.ProductFilterViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductFiltersAdapter(
    private val onFilterClickSubject: PublishSubject<FilterUI>,
    private val onFilterClearClickSubject: PublishSubject<FilterUI>
) : RecyclerView.Adapter<ProductFilterViewHolder>() {

    var filterList = mutableListOf<FilterUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductFilterViewHolder(
        binding = ViewHolderFilterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onFilterClickSubject = onFilterClickSubject,
        onFilterClearClickSubject = onFilterClearClickSubject
    )

    override fun onBindViewHolder(
        holder: ProductFilterViewHolder,
        position: Int
    ) = holder.onBind(filterList[position])

    override fun getItemCount() = filterList.size

}