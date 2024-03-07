package com.vodovoz.app.feature.buy_certificate.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderChooseCertificateBinding
import com.vodovoz.app.databinding.ViewHolderEmailCertificateBinding
import com.vodovoz.app.databinding.ViewHolderMessageCertificateBinding
import com.vodovoz.app.feature.buy_certificate.BuyCertificateViewModel
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI

class BuyCertificateAdapter(
    private val viewModel: BuyCertificateViewModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val UNKNOWN = -1
        private const val CHOOSE_CERTIFICATE = 0
        private const val EMAIL_CERTIFICATE = 1
        private const val MESSAGE_CERTIFICATE = 2
    }

    var propertiesUIList = listOf<BuyCertificatePropertyUI>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(
        position: Int,
    ) = when (propertiesUIList[position].code) {
        "buyMoney" -> CHOOSE_CERTIFICATE
        "email" -> EMAIL_CERTIFICATE
        "opisanie" -> MESSAGE_CERTIFICATE
        else -> UNKNOWN
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = when (viewType) {
        CHOOSE_CERTIFICATE -> ChooseCertificateViewHolder(
            binding = ViewHolderChooseCertificateBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onChangeAmount = {
                viewModel.addResult("quanity", it)
            }
        )

        EMAIL_CERTIFICATE -> EmailCertificateViewHolder(
            binding = ViewHolderEmailCertificateBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

        MESSAGE_CERTIFICATE -> MessageCertificateViewHolder(
            binding = ViewHolderMessageCertificateBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

        else -> throw Exception("Unknown type")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val property = propertiesUIList[position]
        when (property.code) {
            "buyMoney" -> (holder as ChooseCertificateViewHolder).onBind(
                property,
                chooseOnCertificate = {
                    viewModel.addResult(property.code, "${it.id}@${it.name}")
                    property.buyCertificateFieldUIList?.forEach { field ->
                        field.selected = field.id == it.id
                    }
                    notifyDataSetChanged()
                }
            )

            "email" -> (holder as EmailCertificateViewHolder).onBind(
                property,
                onEditText = {
                    viewModel.addResult(property.code, it)
                }
            )

            "opisanie" -> (holder as MessageCertificateViewHolder).onBind(
                property,
                onEditText = {
                    viewModel.addResult(property.code, it)
                }
            )
        }
    }

    override fun getItemCount() = propertiesUIList.size

}