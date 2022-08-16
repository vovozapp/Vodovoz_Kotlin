package com.vodovoz.app.ui.view_holder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderServiceNameBinding

class ServiceNameViewHolder(
    private val binding: ViewHolderServiceNameBinding,
    private val context: Context,
    private val onServiceClick: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener { onServiceClick(serviceData.second) }
    }

    private lateinit var serviceData: Triple<String, String, Boolean>

    fun onBind(serviceData: Triple<String, String, Boolean>) {
        this.serviceData = serviceData
        binding.tvName.text = serviceData.first
        when(serviceData.third) {
            true -> binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.bluePrimary))
            false -> binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
        }
    }

}