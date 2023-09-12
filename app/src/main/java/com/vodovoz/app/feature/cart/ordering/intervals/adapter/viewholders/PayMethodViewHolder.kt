package com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders

import android.view.View
import androidx.core.widget.TextViewCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderPayMethodBinding
import com.vodovoz.app.ui.model.PayMethodUI

class PayMethodViewHolder(
    view: View,
    clickListener: ((Item) -> Unit),
) : ItemViewHolder<PayMethodUI>(view) {

    private val binding: ViewHolderPayMethodBinding = ViewHolderPayMethodBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?: return@setOnClickListener
            clickListener(item)
        }
    }

    override fun bind(item: PayMethodUI) {
        super.bind(item)

        binding.tvName.text = item.name

        when (item.isSelected) {
            true -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalBlue)
            false -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalGray)
        }
    }

}