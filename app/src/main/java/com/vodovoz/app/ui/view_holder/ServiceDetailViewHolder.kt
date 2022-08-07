package com.vodovoz.app.ui.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderServiceDetailBinding
import com.vodovoz.app.ui.model.ServiceUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ServiceDetailViewHolder(
    private val binding: ViewHolderServiceDetailBinding,
    private val onServiceClickSubject: PublishSubject<String>
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.btnMoreInfo.setOnClickListener {
            onServiceClickSubject.onNext(serviceUI.type)
        }
    }

    private lateinit var serviceUI: ServiceUI

    fun onBind(serviceUI: ServiceUI) {
        this.serviceUI = serviceUI

        binding.tvName.text = serviceUI.name
        binding.tvDetails.text = serviceUI.detail
    }

}