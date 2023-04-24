package com.vodovoz.app.feature.cart.ordering.intervals.adapter.viewholders

import android.view.View
import androidx.core.widget.TextViewCompat
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderGenderBinding
import com.vodovoz.app.databinding.ViewHolderPayMethodBinding
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsClickListener
import com.vodovoz.app.ui.model.PayMethodUI

class GenderFlowViewHolder(
    view: View,
    clickListener: IntervalsClickListener
) : ItemViewHolder<GenderUI>(view) {

    private val binding: ViewHolderGenderBinding = ViewHolderGenderBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?:return@setOnClickListener
            val send = if (item.name == "Мужской") {
                "MALE"
            } else {
                "FEMALE"
            }
            clickListener.onGenderClick(send)
        }
    }

    override fun bind(item: GenderUI) {
        super.bind(item)

        binding.tvName.text = item.name

        when(item.isSelected) {
            true -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalBlue)
            false -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalGray)
        }
    }

}