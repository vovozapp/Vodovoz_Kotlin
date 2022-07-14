package com.vodovoz.app.ui.view_holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderFilterBinding
import com.vodovoz.app.ui.extensions.FilterValueTextExtensions.setFilterValue
import com.vodovoz.app.ui.model.FilterUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductFilterViewHolder(
    private val binding: ViewHolderFilterBinding,
    private val onFilterClickSubject: PublishSubject<FilterUI>,
    private val onFilterClearClickSubject: PublishSubject<FilterUI>
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onFilterClickSubject.onNext(filterUI)
        }
        binding.clear.setOnClickListener {
            filterUI.filterValueList.clear()
            fillValue(filterUI)
            onFilterClearClickSubject.onNext(filterUI)
        }
    }

    private lateinit var filterUI: FilterUI

    fun onBind(filterUI: FilterUI) {
        this.filterUI = filterUI
        binding.name.text = filterUI.name
        fillValue(filterUI)
    }

    private fun fillValue(filterUI: FilterUI) {
        with(binding) {
            when(filterUI.filterValueList.size) {
                0 -> {
                    value.visibility = View.GONE
                    clear.visibility = View.INVISIBLE
                }
                else -> {
                    value.visibility = View.VISIBLE
                    clear.visibility = View.VISIBLE
                    value.setFilterValue(filterUI.filterValueList)
                }
            }
        }
    }

}