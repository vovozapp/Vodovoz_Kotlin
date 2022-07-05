package com.vodovoz.app.ui.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderServiceBinding
import com.vodovoz.app.ui.components.view_holder.ServiceViewHolder
import com.vodovoz.app.ui.model.ServiceUI
import io.reactivex.rxjava3.subjects.PublishSubject

class ServicesAdapter(
    private var serviceUIList: List<ServiceUI>,
    private val onServiceClickSubject: PublishSubject<String>
) : RecyclerView.Adapter<ServiceViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ServiceViewHolder(
        binding = ViewHolderServiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onServiceClickSubject = onServiceClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: ServiceViewHolder,
        position: Int
    ) = holder.onBind(serviceUIList[position])

    override fun getItemCount() = serviceUIList.size

}