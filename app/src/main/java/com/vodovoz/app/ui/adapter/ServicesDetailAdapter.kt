package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderServiceDetailBinding
import com.vodovoz.app.ui.model.ServiceUI
import com.vodovoz.app.ui.view_holder.ServiceDetailViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class ServicesDetailAdapter(
    private val onServiceClickSubject: PublishSubject<String>
) : RecyclerView.Adapter<ServiceDetailViewHolder>() {

    var serviceUIList = listOf<ServiceUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ServiceDetailViewHolder(
        binding = ViewHolderServiceDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onServiceClickSubject = onServiceClickSubject
    )

    override fun onBindViewHolder(
        holder: ServiceDetailViewHolder,
        position: Int
    ) = holder.onBind(serviceUIList[position])

    override fun getItemCount() = serviceUIList.size

}