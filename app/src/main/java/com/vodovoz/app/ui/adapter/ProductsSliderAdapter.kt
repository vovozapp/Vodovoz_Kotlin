package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderProductBinding
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view_holder.ProductSliderViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class ProductsSliderAdapter(
    private val onProductClickSubject: PublishSubject<Long>,
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>,
    private val onNotifyWhenBeAvailable: (Long, String, String) -> Unit,
    private val onNotAvailableMore: () -> Unit,
    private val cardWidth: Int
) : RecyclerView.Adapter<ProductSliderViewHolder>() {

    var sliderProductUIList = listOf<ProductUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ProductSliderViewHolder(
        binding = ViewHolderSliderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onProductClickSubject = onProductClickSubject,
        onFavoriteClickSubject = onFavoriteClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onNotAvailableMore = onNotAvailableMore,
        onNotifyWhenBeAvailable = onNotifyWhenBeAvailable,
        context = parent.context,
        cardWidth = cardWidth
    )

    override fun onBindViewHolder(
        holder: ProductSliderViewHolder,
        position: Int
    ) = holder.onBind(sliderProductUIList[position])

    override fun getItemCount() = sliderProductUIList.size

}