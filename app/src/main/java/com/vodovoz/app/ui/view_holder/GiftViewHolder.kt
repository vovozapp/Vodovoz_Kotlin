package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderGiftBinding
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class GiftViewHolder(
    private val binding: ViewHolderGiftBinding,
    private val onPickUpGiftSubject: PublishSubject<ProductUI>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnPickUpGift.setOnClickListener { onPickUpGiftSubject.onNext(productUI) }
    }

    private lateinit var productUI: ProductUI

    fun onBind(productUI: ProductUI) {
        this.productUI = productUI

        binding.tvName.text = productUI.name
        Glide.with(context)
            .load(productUI.detailPicture)
            .into(binding.imgPicture)
    }

}