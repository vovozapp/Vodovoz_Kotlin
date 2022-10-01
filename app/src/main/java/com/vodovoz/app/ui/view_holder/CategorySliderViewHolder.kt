package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderSliderProductCategoryBinding
import com.vodovoz.app.ui.adapter.ProductsSliderAdapter
import com.vodovoz.app.ui.decoration.ProductSliderMarginDecoration
import com.vodovoz.app.ui.diffUtils.ProductSliderDiffUtilCallback
import com.vodovoz.app.ui.model.CategoryDetailUI
import io.reactivex.rxjava3.subjects.PublishSubject

class CategorySliderViewHolder(
    private val binding: ViewHolderSliderProductCategoryBinding,
    private val onProductClickSubject: PublishSubject<Long>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>,
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val onNotifyWhenBeAvailable: (Long, String, String) -> Unit,
    private val onNotAvailableMore: () -> Unit,
    private val context: Context,
    private val cardWidth: Int
) : RecyclerView.ViewHolder(binding.root) {

    private val productsSliderAdapter = ProductsSliderAdapter(
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onFavoriteClickSubject = onFavoriteClickSubject,
        onNotAvailableMore = onNotAvailableMore,
        onNotifyWhenBeAvailable = onNotifyWhenBeAvailable,
        cardWidth = cardWidth,
    )

    init {
        binding.rvProducts.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvProducts.adapter = productsSliderAdapter
    }

    private var isAddItemDecoration = false
    private lateinit var categoryDetailUI: CategoryDetailUI

    fun onBind(categoryDetailUI: CategoryDetailUI, isLast: Boolean) {
        this.categoryDetailUI = categoryDetailUI

        if (!isAddItemDecoration) {
            binding.rvProducts.addItemDecoration(ProductSliderMarginDecoration(
                space = context.resources.getDimension(R.dimen.space_16).toInt(),
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