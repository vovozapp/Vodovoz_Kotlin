package com.vodovoz.app.feature.bottom.services.detail.bottom.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderServiceNameBinding

class ServiceNamesFlowViewHolder(
    view: View,
    clickListener: ServiceNamesFlowClickListener
) : ItemViewHolder<ServiceNameItem>(view) {

    private val binding: ViewHolderServiceNameBinding = ViewHolderServiceNameBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?:return@setOnClickListener
            clickListener.onServiceClick(item.type)
        }
    }

    override fun bind(item: ServiceNameItem) {
        super.bind(item)

        binding.tvName.text = item.name
        when(item.isSelected) {
            true -> binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.bluePrimary))
            false -> binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_gray))
        }
    }
}