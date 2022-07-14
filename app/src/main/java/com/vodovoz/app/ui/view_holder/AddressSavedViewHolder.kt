package com.vodovoz.app.ui.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderAddressSavedBinding
import com.vodovoz.app.ui.model.AddressUI
import io.reactivex.rxjava3.subjects.PublishSubject

class AddressSavedViewHolder(
    private val binding: ViewHolderAddressSavedBinding,
    private val onEditAddressClickSubject: PublishSubject<AddressUI>,
    private val onDeleteAddressClickSubject: PublishSubject<Long>
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.edit.setOnClickListener {
            onEditAddressClickSubject.onNext(addressUI)
        }
        binding.root.setOnLongClickListener {
            addressUI.id?.let { addressId ->
                onDeleteAddressClickSubject.onNext(addressId)
            }
            false
        }
    }

    private lateinit var addressUI: AddressUI

    fun onBind(addressUI: AddressUI) {
        this.addressUI = addressUI
        binding.address.text = StringBuilder()
            .append(addressUI.locality)
            .append(", ")
            .append(addressUI.street)
            .append(", ")
            .append(addressUI.house)
            .toString()

        binding.additionalInfo.text = StringBuilder()
            .append("Под:")
            .append(addressUI.entrance)
            .append(" Этаж:")
            .append(addressUI.floor)
            .append(" Кв:")
            .append(addressUI.flat)
            .toString()
    }

}