package com.vodovoz.app.feature.buy_certificate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderChooseCertificateBinding
import com.vodovoz.app.databinding.ViewHolderEmailCertificateBinding
import com.vodovoz.app.databinding.ViewHolderMessageCertificateBinding
import com.vodovoz.app.feature.buy_certificate.BuyCertificateViewModel

class BuyCertificateAdapter(
    private val viewModel: BuyCertificateViewModel,
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.view_holder_choose_certificate -> ChooseCertificateViewHolder(
                binding = ViewHolderChooseCertificateBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onChangeAmount = {
                    viewModel.addResult("quanity", it)
                },
                onChange = { code, value ->
                    viewModel.addResult(code, value.id)
                }
            )

            R.layout.view_holder_email_certificate -> EmailCertificateViewHolder(
                binding = ViewHolderEmailCertificateBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onEditText = { code, value ->
                    viewModel.addResult(code, value)
                }
            )

            R.layout.view_holder_message_certificate -> MessageCertificateViewHolder(
                binding = ViewHolderMessageCertificateBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onEditText = { code, value ->
                    viewModel.addResult(code, value)
                }
            )

            else -> throw Exception("Unknown type")
        }
    }
}