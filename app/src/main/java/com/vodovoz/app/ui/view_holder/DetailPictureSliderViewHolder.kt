package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderSliderDetailPictureBinding
import io.reactivex.rxjava3.subjects.PublishSubject

class DetailPictureSliderViewHolder(
    private val binding: ViewHolderSliderDetailPictureBinding,
    private val onProductDetailPictureClickSubject: PublishSubject<Long>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.imgPicture.setOnClickListener {
            if (triple.third) onProductDetailPictureClickSubject.onNext(triple.first)
        }
    }

    private lateinit var triple: Triple<Long, String, Boolean>

    fun onBind(triple: Triple<Long, String, Boolean>) {
        this.triple = triple
        when(triple.third) {
            true -> {
                binding.imgPicture.alpha = 1.0f
            }
            false -> {
                binding.imgPicture.alpha = 0.5f
            }
        }
        Glide
            .with(context)
            .load(triple.second)
            .into(binding.imgPicture)
    }

}