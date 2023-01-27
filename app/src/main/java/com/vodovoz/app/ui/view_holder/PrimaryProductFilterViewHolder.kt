package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderBrandFilterValueBinding
import com.vodovoz.app.ui.model.FilterValueUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PrimaryProductFilterViewHolder(
    private val binding: ViewHolderBrandFilterValueBinding,
    private val onFilterClickSubject: PublishSubject<FilterValueUI>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onFilterClickSubject.onNext(filterValue) }
    }

    private lateinit var filterValue: FilterValueUI

    fun onBind(filterValue: FilterValueUI) {
        this.filterValue = filterValue

        binding.tvName.text = filterValue.value

        when(filterValue.isSelected) {
            true -> binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.bluePrimary))
            false -> binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
        }
    }

}