package com.vodovoz.app.ui.components.adapter.allBrandAdapter

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderBrandWithNameBinding
import com.vodovoz.app.ui.model.BrandUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.subjects.PublishSubject

class BrandWithNameViewHolder(
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