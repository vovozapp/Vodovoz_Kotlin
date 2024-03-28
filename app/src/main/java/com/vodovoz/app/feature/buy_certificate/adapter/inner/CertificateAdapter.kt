package com.vodovoz.app.feature.buy_certificate.adapter.inner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.databinding.ItemCertificateImageBinding
import com.vodovoz.app.ui.model.custom.BuyCertificateFieldUI
import com.vodovoz.app.util.extensions.debugLog

class CertificateAdapter(
    list: List<BuyCertificateFieldUI>,
    private val onItemClick: (BuyCertificateFieldUI) -> Unit,
) : RecyclerView.Adapter<CertificateAdapter.ViewHolder>() {

    var list = listOf<BuyCertificateFieldUI>()
        set(value) {
            field = value
        }

    init {
        this.list = list
    }

    class ViewHolder(val binding: ItemCertificateImageBinding) :
        RecyclerView.ViewHolder(binding.root)
//    inner class ViewHolder(val binding: ViewDataBinding) :
//        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        debugLog { "CertificateAdapter onCreateViewHolder" }
        val binding = ItemCertificateImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        debugLog { "CertificateAdapter onBindViewHolder ${item.isSelected}" }
        with(holder.binding.root) {
            if (item.isSelected) {
                setBackgroundResource(R.drawable.border_certificate_image)
            } else {
                setBackgroundResource(R.drawable.border_no_certificate_image)
            }
            Glide.with(context)
                .load(item.imageUrl.parseImagePath())
                .into(this)
            setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}