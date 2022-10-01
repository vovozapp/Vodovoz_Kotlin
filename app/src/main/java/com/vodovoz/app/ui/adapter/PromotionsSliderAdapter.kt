package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderPromotionBinding
import com.vodovoz.app.ui.model.PromotionUI
import com.vodovoz.app.ui.view_holder.PromotionSliderViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionsSliderAdapter(
    private val onPromotionClickSubject: PublishSubject<Long>,
    private val onProductClickSubject: PublishSubject<Long>,
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>>,
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>>,
    private val onNotifyWhenBeAvailable: (Long, String, String) -> Unit,
    private val onNotAvailableMore: () -> Unit,
) : RecyclerView.Adapter<PromotionSliderViewHolder>() {

    var promotionUIList = listOf<PromotionUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PromotionSliderViewHolder(
        binding = ViewHolderSliderPromotionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onPromotionClickSubject = onPromotionClickSubject,
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onFavoriteClickSubject = onFavoriteClickSubject,
        onNotifyWhenBeAvailable = onNotifyWhenBeAvailable,
        onNotAvailableMore = onNotAvailableMore,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: PromotionSliderViewHolder,
        position: Int
    ) = holder.onBind(promotionUIList[position])

    override fun getItemCount() = promotionUIList.size

}