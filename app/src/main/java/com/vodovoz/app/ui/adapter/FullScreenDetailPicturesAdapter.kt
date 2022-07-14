package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderFullScreenDetailPictureBinding
import com.vodovoz.app.ui.view_holder.FullScreenDetailPictureViewHolder

class FullScreenDetailPicturesAdapter(
    val detailPictureList: List<String>
) : RecyclerView.Adapter<FullScreenDetailPictureViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = FullScreenDetailPictureViewHolder(
        binding = ViewHolderFullScreenDetailPictureBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        context = parent.context
    )

    override fun onBindViewHolder(
        holder: FullScreenDetailPictureViewHolder,
        position: Int
    ) = holder.onBind(detailPictureList[position])

    override fun getItemCount() = detailPictureList.size

}