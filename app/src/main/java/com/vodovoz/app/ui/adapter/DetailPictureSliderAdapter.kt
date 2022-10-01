package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderDetailPictureBinding
import com.vodovoz.app.ui.view_holder.DetailPictureSliderViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class DetailPictureSliderAdapter(
    private val onProductDetailPictureClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<DetailPictureSliderViewHolder>() {

    var detailPictureUrlList = listOf<Triple<Long, String, Boolean>>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = DetailPictureSliderViewHolder(
        binding = ViewHolderSliderDetailPictureBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onProductDetailPictureClickSubject = onProductDetailPictureClickSubject,
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: DetailPictureSliderViewHolder,
        position: Int
    ) = holder.onBind(detailPictureUrlList[position])

    override fun getItemCount() = detailPictureUrlList.size

}