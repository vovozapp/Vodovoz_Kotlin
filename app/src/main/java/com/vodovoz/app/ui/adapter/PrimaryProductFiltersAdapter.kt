package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderBrandFilterValueBinding
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.view_holder.PrimaryProductFilterViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class PrimaryProductFiltersAdapter(
    private val onFilterClickSubject: PublishSubject<FilterValueUI>
) : RecyclerView.Adapter<PrimaryProductFilterViewHolder>() {

    var brandFilterValueList = listOf<FilterValueUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PrimaryProductFilterViewHolder(
        binding = ViewHolderBrandFilterValueBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onFilterClickSubject = onFilterClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: PrimaryProductFilterViewHolder,
        position: Int
    ) = holder.onBind(
        brandFilterValueList[position])

    override fun getItemCount() = brandFilterValueList.size

}