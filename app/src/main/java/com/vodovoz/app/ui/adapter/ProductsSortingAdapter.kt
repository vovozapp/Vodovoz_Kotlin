package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.ViewHolderProductsSortingBinding

class ProductsSortingAdapter : RecyclerView.Adapter<ProductsSortingVH>() {

    private var sortingList = listOf<SortType>()
    private var selectedSorting: SortType? = null
    private lateinit var selectSorting: (SortType) -> Unit

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(sortingList: List<SortType>, selectedSorting: SortType) {
        this.sortingList = sortingList
        this.selectedSorting = selectedSorting
        notifyDataSetChanged()
    }

    fun setupListeners(selectSorting: (SortType) -> Unit) {
        this.selectSorting = selectSorting
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
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
        position: Int
    ) = holder.onBind(sortingList[position], sortingList[position] == selectedSorting)

    override fun getItemCount() = sortingList.size

}

class ProductsSortingVH(
    private val binding: ViewHolderProductsSortingBinding,
    private val selectSort: (SortType) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.root.setOnClickListener { selectSort(sortType) } }

    private lateinit var sortType: SortType

    fun onBind(sortType: SortType, isSelected: Boolean) {
        this.sortType = sortType
        binding.tvName.text = sortType.sortName
        when(isSelected) {
            true -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalBlue)
            false -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalGray)
        }
    }

}