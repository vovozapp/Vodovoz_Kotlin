package com.vodovoz.app.ui.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderProductCategoryBinding
import com.vodovoz.app.ui.components.view_holder.CategorySliderViewHolder
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class CategoriesAdapter(
    private val onChangeProductQuantitySubject: PublishSubject<ProductUI>,
    private val onProductClickSubject: PublishSubject<Long>,
    private val cardWidth: Int
) : RecyclerView.Adapter<CategorySliderViewHolder>() {

    var sliderCategoryUIList = listOf<CategoryDetailUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CategorySliderViewHolder(
        ViewHolderSliderProductCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        context = parent.context,
        cardWidth = cardWidth
    )

    override fun onBindViewHolder(
        holder: CategorySliderViewHolder,
        position: Int
    ) = holder.onBind(sliderCategoryUIList[position], position == sliderCategoryUIList.size - 1)

    override fun getItemCount() = sliderCategoryUIList.size

}