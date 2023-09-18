package com.vodovoz.app.feature.service_order.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderServiceOrderFormFieldBinding
import com.vodovoz.app.ui.model.ServiceOrderFormFieldUI

class ServiceOrderFormFieldViewHolder(
    private val binding: ViewHolderServiceOrderFormFieldBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.value.doAfterTextChanged { serviceOrderFormFieldUI.value = it.toString() }
    }

    private lateinit var serviceOrderFormFieldUI: ServiceOrderFormFieldUI

    fun onBind(serviceOrderFormFieldUI: ServiceOrderFormFieldUI) {
        this.serviceOrderFormFieldUI = serviceOrderFormFieldUI

        binding.name.text = serviceOrderFormFieldUI.name
        binding.value.setText(serviceOrderFormFieldUI.defaultValue)

        when(serviceOrderFormFieldUI.isError) {
            true -> binding.name.setTextColor(ContextCompat.getColor(context, R.color.red))
            false -> binding.name.setTextColor(ContextCompat.getColor(context, R.color.blackTextDark))
        }
    }

}