package com.vodovoz.app.ui.components.view_holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderBrandWithNameBinding
import com.vodovoz.app.ui.model.BrandUI
import io.reactivex.rxjava3.subjects.PublishSubject

class BrandJustNameViewHolder(
    private val binding: ViewHolderBrandWithNameBinding,
    private val onBrandClickSubject: PublishSubject<Long>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onBrandClickSubject.onNext(brandUI.id) }
    }

    private lateinit var brandUI: BrandUI

    fun onBind(brandUI: BrandUI) {
        this.brandUI = brandUI
        binding.name.text = brandUI.name
    }

}