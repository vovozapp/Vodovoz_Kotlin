package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderBrandWithNameBinding
import com.vodovoz.app.ui.diffUtils.BrandSliderDiffUtilCallback
import com.vodovoz.app.ui.view_holder.BrandJustNameViewHolder
import com.vodovoz.app.ui.model.BrandUI
import io.reactivex.rxjava3.subjects.PublishSubject

class AllBrandsAdapter(
    private val onBrandClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<BrandJustNameViewHolder>() {

    var lastQuery: String = ""

    var brandUIFullList = listOf<BrandUI>()
        set(value) {
            field = value
            filter(lastQuery)
        }

    private var brandUIFilteredList = mutableListOf<BrandUI>()

    fun filter(query: String) {
        lastQuery = query
        val newFilteredList = brandUIFullList.filter { it.name.contains(query) }
        updateData(newFilteredList)
    }

    private fun updateData(brandUIList: List<BrandUI>) {
        val diffUtil = BrandSliderDiffUtilCallback(
            oldList = brandUIFilteredList,
            newList = brandUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            brandUIFilteredList = brandUIList.toMutableList()
            diffResult.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = BrandJustNameViewHolder(
        binding = ViewHolderBrandWithNameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onBrandClickSubject = onBrandClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: BrandJustNameViewHolder,
        position: Int
    ) = holder.onBind(brandUIFilteredList[position])

    override fun getItemCount() = brandUIFilteredList.size

}