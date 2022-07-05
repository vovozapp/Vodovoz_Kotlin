package com.vodovoz.app.ui.components.view_holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderServiceBinding
import com.vodovoz.app.ui.model.ServiceUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ServiceViewHolder(
    private val binding: ViewHolderServiceBinding,
    private val onServiceClickSubject: PublishSubject<String>,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onServiceClickSubject.onNext(serviceUI.type)
        }
    }

    private lateinit var serviceUI: ServiceUI

    fun onBind(serviceUI: ServiceUI) {
        this.serviceUI = serviceUI

        binding.name.text = serviceUI.name
        binding.price.text = serviceUI.price

        Glide.with(context)
            .load(serviceUI.detailPicture)
            .into(binding.picture)
    }
}