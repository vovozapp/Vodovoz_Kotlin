package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderPromotionProductBinding
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view_holder.PromotionProductSliderViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionProductsSliderAdapter(
    private val onProductClickSubject: PublishSubject<Long>,
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>,
    private val onNotifyWhenBeAvailable: (Long, String, String) -> Unit,
    private val onNotAvailableMore: () -> Unit,
) : RecyclerView.Adapter<PromotionProductSliderViewHolder>() {

    var productUIList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PromotionProductSliderViewHolder(
        binding = ViewHolderSliderPromotionProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onFavoriteClickSubject = onFavoriteClickSubject,
        onNotAvailableMore = onNotAvailableMore,
        onNotifyWhenBeAvailable = onNotifyWhenBeAvailable,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: PromotionProductSliderViewHolder,
        position: Int
    ) = holder.onBind(productUIList[position])

    override fun getItemCount() = productUIList.size

}