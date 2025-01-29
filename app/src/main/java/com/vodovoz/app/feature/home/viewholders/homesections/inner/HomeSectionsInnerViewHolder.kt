package com.vodovoz.app.feature.home.viewholders.homesections.inner

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.databinding.ViewHolderSectionTopBinding
import com.vodovoz.app.ui.model.SectionDataUI
import com.vodovoz.app.util.extensions.debugLog

class HomeSectionsInnerViewHolder(
    view: View,
    private val clickListener: (SectionDataUI) -> Unit,
) : ItemViewHolder<SectionDataUI>(view) {

    private val binding: ViewHolderSectionTopBinding = ViewHolderSectionTopBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = getItemByPosition() ?: return@setOnClickListener
            clickListener(item)
        }
    }

    override fun bind(item: SectionDataUI) {
        super.bind(item)
        debugLog { "item: $item"}

        binding.sectionTextView.text = item.name

        Glide
            .with(itemView.context)
            .load(item.imageUrl.parseImagePath())
            .into(binding.sectionImageView)
    }

    private fun getItemByPosition(): SectionDataUI? {
        return (bindingAdapter as? HomeSectionsInnerAdapter)?.getItem(bindingAdapterPosition) as? SectionDataUI
    }
}