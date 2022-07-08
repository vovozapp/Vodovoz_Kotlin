package com.vodovoz.app.ui.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderSliderDetailPictureBinding
import com.vodovoz.app.ui.components.view_holder.DetailPicturePagerViewHolder
import com.vodovoz.app.ui.components.interfaces.IOnProductDetailPictureClick
import com.vodovoz.app.ui.components.view_holder.DetailPictureSliderViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class DetailPictureSliderAdapter(
    private val onProductDetailPictureClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<DetailPictureSliderViewHolder>() {

    var detailPictureUrlList = listOf<Pair<Long, String>>()

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