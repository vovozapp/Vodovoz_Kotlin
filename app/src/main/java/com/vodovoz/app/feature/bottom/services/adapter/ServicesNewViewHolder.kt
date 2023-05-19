package com.vodovoz.app.feature.bottom.services.adapter

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.databinding.ViewHolderServiceDetailBinding
import com.vodovoz.app.databinding.ViewHolderServiceDetailNewBinding
import com.vodovoz.app.feature.bottom.services.newservs.model.ServiceNew
import com.vodovoz.app.ui.model.ServiceUI

class ServicesNewViewHolder(
    view: View,
    clickListener: ServicesClickListener
) : ItemViewHolder<ServiceNew>(view) {

    private val binding: ViewHolderServiceDetailNewBinding = ViewHolderServiceDetailNewBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener.onItemClick(item)
        }
    }

    override fun bind(item: ServiceNew) {
        super.bind(item)

        binding.titleTv.text = item.name
        Glide.with(itemView.context)
            .load(parseImagePath(item.preview))
            .placeholder(R.drawable.placeholderimageproduits)
            .error(R.drawable.placeholderimageproduits)
            .into(binding.logoIv)


    }

    fun parseImagePath(string: String?) = StringBuilder()
        .append("http://mvodovoz.tw1.ru/")
        .append(string?.replace("\"", ""))
        .toString()
}