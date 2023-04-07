package com.vodovoz.app.feature.bottom.aboutapp.adapter

import android.view.View
import androidx.core.content.ContextCompat
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderAboutAppActionBinding

class AboutAppFlowViewHolder(
    view: View,
    clickListener: AboutAppClickListener
) : ItemViewHolder<AboutApp>(view) {

    private val binding: ViewHolderAboutAppActionBinding = ViewHolderAboutAppActionBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?:return@setOnClickListener
            clickListener.onActionClick(item)
        }
    }

    override fun bind(item: AboutApp) {
        super.bind(item)

        binding.imgActionImage.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.actionImgResId))
        binding.tvAction.text = itemView.context.getString(item.actionNameResId)
    }
}