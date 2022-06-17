package com.vodovoz.app.ui.components.adapter.brandFilterValuesAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderBrandFilterValueBinding
import com.vodovoz.app.ui.model.FilterValueUI
import io.reactivex.rxjava3.subjects.PublishSubject

class BrandFilterValuesAdapter(
    private val onBrandClickSubject: PublishSubject<FilterValueUI>
) : RecyclerView.Adapter<BrandFilterValueViewHolder>() {

    var brandFilterValueList = listOf<FilterValueUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = BrandFilterValueViewHolder(
        binding = ViewHolderBrandFilterValueBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onBrandClickSubject = onBrandClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: BrandFilterValueViewHolder,
        position: Int
    ) = holder.onBind(
        brandFilterValueList[position])

    override fun getItemCount() = brandFilterValueList.size

}