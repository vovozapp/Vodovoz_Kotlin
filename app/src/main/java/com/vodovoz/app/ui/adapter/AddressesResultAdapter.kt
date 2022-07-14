package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderAddressResultBinding
import com.vodovoz.app.ui.view_holder.AddressResultViewHolder
import com.yandex.mapkit.geometry.Point
import io.reactivex.rxjava3.subjects.PublishSubject

class AddressesResultAdapter(
    private val onAddressClickSubject: PublishSubject<Pair<String, Point>>
) : RecyclerView.Adapter<AddressResultViewHolder>() {

    var addressList = listOf<Pair<String, Point>>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AddressResultViewHolder(
        binding = ViewHolderAddressResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onAddressClickSubject = onAddressClickSubject
    )

    override fun onBindViewHolder(
        holder: AddressResultViewHolder,
        position: Int
    ) = holder.onBind(addressList[position])

    override fun getItemCount() = addressList.size

}