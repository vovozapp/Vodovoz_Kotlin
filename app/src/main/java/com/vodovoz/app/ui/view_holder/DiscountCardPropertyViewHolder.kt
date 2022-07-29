package com.vodovoz.app.ui.view_holder

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderDiscountCardPropertyBinding
import com.vodovoz.app.ui.model.custom.DiscountCardPropertyUI

class DiscountCardPropertyViewHolder(
    private val binding: ViewHolderDiscountCardPropertyBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var discountCardPropertyUI: DiscountCardPropertyUI

    fun onBind(discountCardPropertyUI: DiscountCardPropertyUI) {
        this.discountCardPropertyUI = discountCardPropertyUI
        binding.value.setMask("********************************")
        binding.name.text = discountCardPropertyUI.name
        binding.value.setText(discountCardPropertyUI.value)

        when(discountCardPropertyUI.isValid) {
            false -> binding.name.setTextColor(ContextCompat.getColor(context, R.color.red))
            true -> binding.name.setTextColor(ContextCompat.getColor(context, R.color.blackTextPrimary))
        }

        if (discountCardPropertyUI.code == "TELEFON") {
            binding.value.setMask("+7-###-###-##-##")
        }

        binding.value.doAfterTextChanged { value ->
            discountCardPropertyUI.value = value.toString()
        }
    }

}