package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderAddressBinding
import com.vodovoz.app.databinding.ViewHolderAddressesTypeTitleBinding
import com.vodovoz.app.ui.model.AddressUI
import io.reactivex.rxjava3.subjects.PublishSubject
import java.lang.Exception

enum class AddressesAdapterItemType(val value: Int) {
    ADDRESSES_TYPE_TITLE(0), ADDRESS(1)
}

sealed class AddressesAdapterItem(val type: AddressesAdapterItemType) {
    class AddressesTypeTitle(
        val title: String
    ) : AddressesAdapterItem(AddressesAdapterItemType.ADDRESSES_TYPE_TITLE)
    class Address(
        val addressUI: AddressUI
    ) : AddressesAdapterItem(AddressesAdapterItemType.ADDRESS)
}

class AddressesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList = listOf<AddressesAdapterItem>()
    private lateinit var deleteAddress: (AddressUI) -> Unit
    private lateinit var editAddress: (AddressUI) -> Unit
    private lateinit var onAddressClick: (AddressUI) -> Unit

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(itemList: List<AddressesAdapterItem>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }

    fun setupListeners(
        deleteAddress: (AddressUI) -> Unit,
        editAddress: (AddressUI) -> Unit,
        onAddressClick: (AddressUI) -> Unit
    ) {
        this.deleteAddress = deleteAddress
        this.editAddress = editAddress
        this.onAddressClick = onAddressClick
    }

    override fun getItemViewType(position: Int) = when(val type = itemList[position].type) {
        AddressesAdapterItemType.ADDRESS -> type.value
        AddressesAdapterItemType.ADDRESSES_TYPE_TITLE -> type.value
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when(viewType) {
        AddressesAdapterItemType.ADDRESS.value -> AddressVH(
            binding = ViewHolderAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            deleteAddress = deleteAddress,
            editAddress = editAddress,
            onAddressClick = onAddressClick
        )
        AddressesAdapterItemType.ADDRESSES_TYPE_TITLE.value -> AddressesTypeTitleVH(
            binding = ViewHolderAddressesTypeTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
        else -> throw Exception("Unknown type")
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) = when(getItemViewType(position)) {
        AddressesAdapterItemType.ADDRESS.value -> (holder as AddressVH).onBind(
            (itemList[position] as AddressesAdapterItem.Address).addressUI)
        AddressesAdapterItemType.ADDRESSES_TYPE_TITLE.value -> (holder as AddressesTypeTitleVH).onBind(
            (itemList[position] as AddressesAdapterItem.AddressesTypeTitle).title)
        else -> throw Exception("Unknown type")
    }

    override fun getItemCount() = itemList.size

}

class AddressesTypeTitleVH(
    private val binding: ViewHolderAddressesTypeTitleBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(title: String) {
        binding.tvTitle.text = title
    }

}

class AddressVH(
    private val binding: ViewHolderAddressBinding,
    private val deleteAddress: (AddressUI) -> Unit,
    private val editAddress: (AddressUI) -> Unit,
    private val onAddressClick: (AddressUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.imgEdit.setOnClickListener { editAddress(addressUI) }
        binding.root.setOnClickListener { onAddressClick(addressUI) }
        binding.root.setOnLongClickListener {
            deleteAddress(addressUI)
            false
        }
    }

    private lateinit var addressUI: AddressUI

    fun onBind(addressUI: AddressUI) {
        this.addressUI = addressUI
        binding.tvAddress.text = StringBuilder()
            .append(addressUI.locality)
            .append(", ")
            .append(addressUI.street)
            .append(", ")
            .append(addressUI.house)
            .toString()

        binding.tvAdditionalInfo.text = StringBuilder()
            .append("Под:")
            .append(addressUI.entrance)
            .append(" Этаж:")
            .append(addressUI.floor)
            .append(" Кв:")
            .append(addressUI.flat)
            .toString()
    }

}