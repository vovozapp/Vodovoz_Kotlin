package com.vodovoz.app.feature.profile.discountcard.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderDiscountCardPropertyBinding
import com.vodovoz.app.ui.model.custom.DiscountCardPropertyUI

class DiscountCardViewHolder(
    view: View,
    val clickListener: DiscountCardClickListener
) : ItemViewHolder<DiscountCardPropertyUI>(view) {

    private val binding: ViewHolderDiscountCardPropertyBinding =
        ViewHolderDiscountCardPropertyBinding.bind(view)

    override fun bind(item: DiscountCardPropertyUI) {
        super.bind(item)

        binding.value.setMask("********************************")
        binding.name.text = item.name
        binding.value.setText(item.value)

        when (item.isValid) {
            false -> binding.name.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.red
                )
            )
            true -> binding.name.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.blackTextDark
                )
            )
        }

        if (item.code == "TELEFON") {
            binding.value.setMask("+7-###-###-##-##")
        }

        binding.value.doAfterTextChanged { value ->
            clickListener.onCardValueChange(value.toString())
        }
    }
}