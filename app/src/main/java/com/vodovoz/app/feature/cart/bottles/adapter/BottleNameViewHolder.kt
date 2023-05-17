package com.vodovoz.app.feature.cart.bottles.adapter

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderBottleNameBinding
import com.vodovoz.app.ui.model.BottleUI
import io.reactivex.rxjava3.subjects.PublishSubject

class BottleNameViewHolder(
    private val binding: ViewHolderBottleNameBinding,
    private val onBottleClickSubject: PublishSubject<Long>
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onBottleClickSubject.onNext(bottleUI.id) }
    }

    private lateinit var bottleUI: BottleUI

    fun onBind(bottleUI: BottleUI) {
        this.bottleUI = bottleUI
        binding.name.text = bottleUI.name
    }

}