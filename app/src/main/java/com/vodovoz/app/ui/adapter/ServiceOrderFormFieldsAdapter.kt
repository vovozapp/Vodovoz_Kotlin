package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderServiceOrderFormFieldBinding
import com.vodovoz.app.ui.model.ServiceOrderFormFieldUI
import com.vodovoz.app.ui.view_holder.ServiceOrderFormFieldViewHolder

class ServiceOrderFormFieldsAdapter() : RecyclerView.Adapter<ServiceOrderFormFieldViewHolder>() {

    var serviceOrderFormFieldUIList = listOf<ServiceOrderFormFieldUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = ServiceOrderFormFieldViewHolder(
        binding = ViewHolderServiceOrderFormFieldBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: ServiceOrderFormFieldViewHolder,
        position: Int
    ) = holder.onBind(serviceOrderFormFieldUIList[position])

    override fun getItemCount() = serviceOrderFormFieldUIList.size

}