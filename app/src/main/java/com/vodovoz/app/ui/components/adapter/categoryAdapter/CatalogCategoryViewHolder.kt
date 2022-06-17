package com.vodovoz.app.ui.components.adapter.categoryAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderCatalogCategoryBinding
import com.vodovoz.app.ui.extensions.CatalogTitleExtensions.setNameWithIndent
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.subjects.PublishSubject

class CatalogCategoryViewHolder(
    private val binding: ViewHolderCatalogCategoryBinding,
    private val context: Context,
    private val categoryClickSubject: PublishSubject<CategoryUI>,
    private val nestingPosition: Int
) : RecyclerView.ViewHolder(binding.root) {

    private val catalogCategoryAdapter = CatalogCategoryAdapter(
        categoryClickSubject = categoryClickSubject,
        nestingPosition = nestingPosition + 1
    )

    private lateinit var categoryUI: CategoryUI

    init {
        binding.subcategoryRecycler.layoutManager = LinearLayoutManager(context)
        binding.subcategoryRecycler.adapter = catalogCategoryAdapter

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

        categoryUI.detailPicture?.let {
            Glide
                .with(context)
                .load(categoryUI.detailPicture)
                .into(binding.image)
        }

        catalogCategoryAdapter.categoryUIList = categoryUI.categoryUIList
        catalogCategoryAdapter.notifyDataSetChanged()
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
        binding.detailController.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.image_plus))
        binding.subcategoryRecycler.visibility = View.GONE
    }

    private fun showDetailCategoryRecycler() {
        binding.detailController.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.image_minus))
        binding.subcategoryRecycler.visibility = View.VISIBLE
    }

}