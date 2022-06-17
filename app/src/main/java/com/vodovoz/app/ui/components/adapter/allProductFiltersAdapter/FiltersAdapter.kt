package com.vodovoz.app.ui.components.adapter.allProductFiltersAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderFilterBinding
import com.vodovoz.app.ui.model.FilterUI
import io.reactivex.rxjava3.subjects.PublishSubject

class FiltersAdapter(
    private val onFilterClickSubject: PublishSubject<FilterUI>,
    private val onFilterClearClickSubject: PublishSubject<FilterUI>
) : RecyclerView.Adapter<FilterViewHolder>() {

    var filterList = mutableListOf<FilterUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = FilterViewHolder(
        binding = ViewHolderFilterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onFilterClickSubject = onFilterClickSubject,
        onFilterClearClickSubject = onFilterClearClickSubject
    )

    override fun onBindViewHolder(
        holder: FilterViewHolder,
        position: Int
    ) = holder.onBind(filterList[position])

    override fun getItemCount() = filterList.size

}