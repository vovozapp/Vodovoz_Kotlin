package com.vodovoz.app.ui.components.adapter.brandFilterValuesAdapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderBrandFilterValueBinding
import com.vodovoz.app.ui.model.FilterValueUI
import io.reactivex.rxjava3.subjects.PublishSubject

class BrandFilterValueViewHolder(
    private val binding: ViewHolderBrandFilterValueBinding,
    private val onBrandClickSubject: PublishSubject<FilterValueUI>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onBrandClickSubject.onNext(filterValue) }
    }

    private lateinit var filterValue: FilterValueUI

    fun onBind(filterValue: FilterValueUI) {
        this.filterValue = filterValue

        binding.name.text = filterValue.value

        if (filterValue.isSelected) {
            binding.name.setTextColor(ContextCompat.getColor(context, R.color.bluePrimary))
        }
    }

}