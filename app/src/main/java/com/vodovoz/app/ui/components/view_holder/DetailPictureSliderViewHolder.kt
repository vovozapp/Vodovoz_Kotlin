package com.vodovoz.app.ui.components.view_holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderDetailPictureBinding
import com.vodovoz.app.ui.components.interfaces.IOnProductClick
import com.vodovoz.app.ui.components.interfaces.IOnProductDetailPictureClick
import io.reactivex.rxjava3.subjects.PublishSubject

class DetailPictureSliderViewHolder(
    private val binding: ViewHolderSliderDetailPictureBinding,
    private val onProductDetailPictureClickSubject: PublishSubject<Long>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.detailPicture.setOnClickListener { onProductDetailPictureClickSubject.onNext(pair.first) }
    }

    private lateinit var pair: Pair<Long, String>

    fun onBind(pair: Pair<Long, String>) {
        this.pair = pair
        Glide
            .with(context)
            .load(pair.second)
            .into(binding.detailPicture)
    }

}