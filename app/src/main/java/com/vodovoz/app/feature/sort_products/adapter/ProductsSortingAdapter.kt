package com.vodovoz.app.feature.sort_products.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderProductsSortingBinding
import com.vodovoz.app.ui.model.SortTypeUI

class ProductsSortingAdapter : RecyclerView.Adapter<ProductsSortingVH>() {

    private var sortingList = listOf<SortTypeUI>()
    private var selectedSorting: SortTypeUI? = null
    private lateinit var selectSorting: (SortTypeUI) -> Unit

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(sortingList: List<SortTypeUI>, selectedSorting: SortTypeUI) {
        this.sortingList = sortingList
        this.selectedSorting = selectedSorting
        notifyDataSetChanged()
    }

    fun setupListeners(selectSorting: (SortTypeUI) -> Unit) {
        this.selectSorting = selectSorting
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = ProductsSortingVH(
        ViewHolderProductsSortingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) { sortType ->
        val oldSelectedPosition = sortingList.indexOf(selectedSorting)
        val newSelectedPosition = sortingList.indexOf(sortType)
        selectedSorting = sortType
        notifyItemChanged(oldSelectedPosition)
        notifyItemChanged(newSelectedPosition)
        selectSorting(sortType)
    }

    override fun onBindViewHolder(
        holder: ProductsSortingVH,
        position: Int,
    ) = holder.onBind(sortingList[position], sortingList[position] == selectedSorting)

    override fun getItemCount() = sortingList.size

}

class ProductsSortingVH(
    private val binding: ViewHolderProductsSortingBinding,
    private val selectSort: (SortTypeUI) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { selectSort(sortType) }
    }

    private lateinit var sortType: SortTypeUI

    fun onBind(sortType: SortTypeUI, isSelected: Boolean) {
        this.sortType = sortType
        binding.tvName.text = sortType.sortName
        binding.radioButton.isChecked = isSelected
    }

}