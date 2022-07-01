package com.vodovoz.app.ui.components.view_holder

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderProductCategoryBinding
import com.vodovoz.app.ui.components.adapter.ProductsSliderAdapter
import com.vodovoz.app.ui.components.decoration.ProductSliderMarginDecoration
import com.vodovoz.app.ui.components.diffUtils.ProductSliderDiffUtilCallback
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class CategorySliderViewHolder(
    private val binding: ViewHolderSliderProductCategoryBinding,
    private val onProductClickSubject: PublishSubject<Long>,
    private val onChangeProductQuantitySubject: PublishSubject<ProductUI>,
    private val context: Context,
    private val cardWidth: Int
) : RecyclerView.ViewHolder(binding.root) {

    private val productsSliderAdapter = ProductsSliderAdapter(
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        cardWidth = cardWidth
    )

    init {
        binding.productRecycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.productRecycler.adapter = productsSliderAdapter
    }

    private var isAddItemDecoration = false
    private lateinit var categoryDetailUI: CategoryDetailUI

    fun onBind(categoryDetailUI: CategoryDetailUI, isLast: Boolean) {
        this.categoryDetailUI = categoryDetailUI

        if (!isAddItemDecoration) {
            binding.productRecycler.addItemDecoration(ProductSliderMarginDecoration(
                space = context.resources.getDimension(R.dimen.primary_space).toInt(),
                itemCount = categoryDetailUI.productUIList.size,
                isLast = isLast
            ))
            isAddItemDecoration = true
        }

        val diffUtil = ProductSliderDiffUtilCallback(
            oldList = productsSliderAdapter.sliderProductUIList,
            newList = categoryDetailUI.productUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            productsSliderAdapter.sliderProductUIList = categoryDetailUI.productUIList
            diffResult.dispatchUpdatesTo(productsSliderAdapter)
        }
    }

}