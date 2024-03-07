package com.vodovoz.app.feature.buy_certificate.adapter

import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderMessageCertificateBinding
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI

class MessageCertificateViewHolder(
    private val binding: ViewHolderMessageCertificateBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(
        property: BuyCertificatePropertyUI,
        onEditText: (String) -> Unit
    ) {
        var addStar = ""
        if (property.required) {
            addStar = "*"
        }
        binding.name.text = buildString {
            append(property.name)
            append(addStar)
        }

        binding.editTextMessage.hint = property.value

        binding.editTextMessage.doAfterTextChanged{
            onEditText(it.toString())
        }

        if(property.text.isNotEmpty()){
            binding.txtViewText.text = property.text
        } else {
            binding.txtViewText.visibility = View.GONE
        }
    }
}