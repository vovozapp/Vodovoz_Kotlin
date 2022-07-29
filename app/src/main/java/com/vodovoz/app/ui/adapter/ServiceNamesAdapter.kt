package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderServiceNameBinding
import com.vodovoz.app.ui.view_holder.ServiceNameViewHolder

class ServiceNamesAdapter(
    private val onServiceClick: (String) -> Unit
) : RecyclerView.Adapter<ServiceNameViewHolder>() {

    var serviceDataList = listOf<Pair<String, String>>()
    var selectedServiceType: String = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ServiceNameViewHolder(
        binding = ViewHolderServiceNameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        context = parent.context,
        onServiceClick = onServiceClick
    )

    override fun onBindViewHolder(
        holder: ServiceNameViewHolder,
        position: Int
    ) = holder.onBind(Triple(
        serviceDataList[position].first,
        serviceDataList[position].second,
        selectedServiceType == serviceDataList[position].second)
    )

    override fun getItemCount() = serviceDataList.size

}