package com.vodovoz.app.ui.components.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderAddressResultBinding
import com.yandex.mapkit.geometry.Point
import io.reactivex.rxjava3.subjects.PublishSubject

class AddressResultViewHolder(
    private val binding: ViewHolderAddressResultBinding,
    private val onAddressClickSubject: PublishSubject<Pair<String, Point>>
) : RecyclerView.ViewHolder(binding.address) {

    init {
        binding.address.setOnClickListener {
            onAddressClickSubject.onNext(pair)
        }
    }

    private lateinit var pair: Pair<String, Point>

    fun onBind(pair: Pair<String, Point>) {
        this.pair = pair
        binding.address.text = pair.first
    }

}