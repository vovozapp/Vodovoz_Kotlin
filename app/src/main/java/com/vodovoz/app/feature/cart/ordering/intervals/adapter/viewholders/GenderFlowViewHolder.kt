package com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders

import android.view.View
import androidx.core.widget.TextViewCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderGenderBinding

class GenderFlowViewHolder(
    view: View,
    clickListener: ((Item) -> Unit),
) : ItemViewHolder<GenderUI>(view) {

    private val binding: ViewHolderGenderBinding = ViewHolderGenderBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener(item)
        }
    }

    override fun bind(item: GenderUI) {
        super.bind(item)

        binding.tvName.text = item.name

        when (item.isSelected) {
            true -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalBlue)
            false -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalGray)
        }
    }

}