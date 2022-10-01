package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderProductCategoryBinding
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.view_holder.CategorySliderViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class CategoriesAdapter(
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val onProductClickSubject: PublishSubject<Long>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>,
    private val onNotifyWhenBeAvailable: (Long, String, String) -> Unit,
    private val onNotAvailableMore: () -> Unit,
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
        onFavoriteClickSubject = onFavoriteClickSubject,
        onNotAvailableMore = onNotAvailableMore,
        onNotifyWhenBeAvailable = onNotifyWhenBeAvailable,
        context = parent.context,
        cardWidth = cardWidth
    )

    override fun onBindViewHolder(
        holder: CategorySliderViewHolder,
        position: Int
    ) = holder.onBind(sliderCategoryUIList[position], position == sliderCategoryUIList.size - 1)

    override fun getItemCount() = sliderCategoryUIList.size

}