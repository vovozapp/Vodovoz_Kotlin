package com.vodovoz.app.feature.buy_certificate.adapter

import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderEmailCertificateBinding
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI

class EmailCertificateViewHolder(
    private val binding: ViewHolderEmailCertificateBinding,
) : RecyclerView.ViewHolder(binding.root) {


    fun onBind(
        property: BuyCertificatePropertyUI,
        onEditText: (String) -> Unit,
    ) {
        var addStar = ""
        if (property.required) {
            addStar = "*"
        }
        binding.name.text = buildString {
            append(property.name)
            append(addStar)
        }

        binding.email.doAfterTextChanged {
            onEditText(it.toString())
        }

        binding.email.hint = property.value

        if (property.text.isNotEmpty()) {
            binding.txtViewText.text = property.text
        } else {
            binding.txtViewText.visibility = View.GONE
        }
    }
}