package com.vodovoz.app.feature.catalog.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.extensions.CatalogTitleExtensions.setNameWithIndent
import com.vodovoz.app.ui.model.CategoryUI

class CatalogFlowViewHolder(
    view: View,
    clickListener: CatalogFlowClickListener,
    val nestingPosition: Int
) : ItemViewHolder<CategoryUI>(view) {

    private val binding: ViewHolderCatalogCategoryBinding = ViewHolderCatalogCategoryBinding.bind(view)

    private val adapter = CatalogFlowAdapter(nestingPosition + 1, clickListener)

    init {

        binding.subcategoryRecycler.layoutManager = LinearLayoutManager(itemView.context)
        binding.subcategoryRecycler.adapter = adapter

        binding.root.setOnClickListener {
            val itemId = item?.id ?: return@setOnClickListener
            clickListener.onCategoryClick(itemId)
        }

        binding.detailController.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            item.isOpen = !item.isOpen
            updateCategoryRecycler(item)
        }
    }

    override fun bind(item: CategoryUI) {
        super.bind(item)

        val nesting = when(nestingPosition) {
            1 -> 0
            else -> nestingPosition
        }
        binding.name.setNameWithIndent(item.name, nesting)

        if (nestingPosition == 0) {
            binding.name.textSize = 16f
        } else {
            binding.name.textSize = 14f
        }

        item.detailPicture?.let {
            Glide
                .with(itemView.context)
                .load(item.detailPicture)
                .into(binding.image)
        }

        adapter.submitList(item.categoryUIList)

        updateCategoryRecycler(item)
    }

    private fun updateCategoryRecycler(categoryUI: CategoryUI) {
        when(categoryUI.categoryUIList.isEmpty()) {
            true -> binding.detailController.visibility = View.INVISIBLE
            false -> binding.detailController.visibility = View.VISIBLE
        }

        when(categoryUI.isOpen) {
            true -> showDetailCategoryRecycler()
            false -> hideDetailCategoryRecycler()
        }
    }

    private fun hideDetailCategoryRecycler() {
        binding.detailController.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_plus))
        binding.subcategoryRecycler.visibility = View.GONE
    }

    private fun showDetailCategoryRecycler() {
        binding.detailController.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_minus))
        binding.subcategoryRecycler.visibility = View.VISIBLE
    }

    private fun getItemByPosition(): CategoryUI? {
        return (bindingAdapter as? CatalogFlowAdapter)?.getItem(bindingAdapterPosition) as? CategoryUI
    }
}