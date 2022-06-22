package com.vodovoz.app.ui.components.fragment.productSlider.categoryDetailAdapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderProductCategoryBinding
import com.vodovoz.app.ui.components.fragment.productSlider.productAdapter.ProductSliderAdapter
import com.vodovoz.app.ui.components.fragment.productSlider.productAdapter.ProductSliderDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.productSlider.productAdapter.ProductSliderMarginDecoration
import com.vodovoz.app.ui.model.CategoryDetailUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductCategorySliderViewHolder(
    private val binding: ViewHolderSliderProductCategoryBinding,
    private val onProductClickSubject: PublishSubject<Long>,
    private val context: Context,
    private val cardWidth: Int
) : RecyclerView.ViewHolder(binding.root) {

    private val productSliderAdapter = ProductSliderAdapter(
        onProductClickSubject = onProductClickSubject,
        cardWidth = cardWidth
    )

    init {
        binding.productRecycler.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.productRecycler.adapter = productSliderAdapter
    }

    private lateinit var categoryDetailUI: CategoryDetailUI

    fun onBind(categoryDetailUI: CategoryDetailUI, isLast: Boolean) {
        this.categoryDetailUI = categoryDetailUI

        binding.productRecycler.addItemDecoration(ProductSliderMarginDecoration(
            space = context.resources.getDimension(R.dimen.primary_space).toInt(),
            itemCount = categoryDetailUI.productUIList.size,
            isLast = isLast
        ))

        val diffUtil = ProductSliderDiffUtilCallback(
            oldList = productSliderAdapter.sliderProductUIList,
            newList = categoryDetailUI.productUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            productSliderAdapter.sliderProductUIList = categoryDetailUI.productUIList
            diffResult.dispatchUpdatesTo(productSliderAdapter)
        }
    }

}