package com.vodovoz.app.ui.view_holder

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryBinding
import com.vodovoz.app.ui.adapter.MainCatalogAdapter
import com.vodovoz.app.ui.extensions.CatalogTitleExtensions.setNameWithIndent
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.util.extensions.sp
import io.reactivex.rxjava3.subjects.PublishSubject

class DropdownCategoryViewHolder(
    private val binding: ViewHolderCatalogCategoryBinding,
    private val context: Context,
    private val categoryClickSubject: PublishSubject<CategoryUI>,
    private val nestingPosition: Int
) : RecyclerView.ViewHolder(binding.root) {

    private val mainCatalogAdapter = MainCatalogAdapter(
        categoryClickSubject = categoryClickSubject,
        nestingPosition = nestingPosition + 1
    )

    private lateinit var categoryUI: CategoryUI

    init {
        binding.subcategoryRecycler.layoutManager = LinearLayoutManager(context)
        binding.subcategoryRecycler.adapter = mainCatalogAdapter

        binding.root.setOnClickListener { categoryClickSubject.onNext(categoryUI) }
        binding.detailController.setOnClickListener {
            categoryUI.isOpen = !categoryUI.isOpen
            updateCategoryRecycler()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(categoryUI: CategoryUI) {
        this.categoryUI = categoryUI

        val nesting = when(nestingPosition) {
            1 -> 0
            else -> nestingPosition
        }
        binding.name.setNameWithIndent(categoryUI.name, nesting)

        if (nestingPosition == 0) {
            binding.name.textSize = 16f
        } else {
            binding.name.textSize = 14f
        }

        categoryUI.detailPicture?.let {
            Glide
                .with(context)
                .load(categoryUI.detailPicture)
                .into(binding.image)
        }

        mainCatalogAdapter.categoryUIList = categoryUI.categoryUIList
        mainCatalogAdapter.notifyDataSetChanged()
        updateCategoryRecycler()
    }

    private fun updateCategoryRecycler() {
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
        binding.detailController.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_plus))
        binding.subcategoryRecycler.visibility = View.GONE
    }

    private fun showDetailCategoryRecycler() {
        binding.detailController.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_minus))
        binding.subcategoryRecycler.visibility = View.VISIBLE
    }

}