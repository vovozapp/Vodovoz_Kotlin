package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderAddressSavedBinding
import com.vodovoz.app.ui.view_holder.AddressSavedViewHolder
import com.vodovoz.app.ui.model.AddressUI
import io.reactivex.rxjava3.subjects.PublishSubject

class AddressesSavedAdapter(
    private val onEditAddressClickSubject: PublishSubject<AddressUI>,
    private val onDeleteAddressClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<AddressSavedViewHolder>() {

    var addressUIList = listOf<AddressUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AddressSavedViewHolder(
        binding = ViewHolderAddressSavedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onEditAddressClickSubject = onEditAddressClickSubject,
        onDeleteAddressClickSubject = onDeleteAddressClickSubject
    )

    override fun onBindViewHolder(
        holder: AddressSavedViewHolder,
        position: Int
    ) = holder.onBind(addressUIList[position])

    override fun getItemCount() = addressUIList.size

}