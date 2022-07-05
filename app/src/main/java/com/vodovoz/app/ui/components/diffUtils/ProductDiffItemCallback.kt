package com.vodovoz.app.ui.components.diffUtils

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.ProductUI

class ProductDiffItemCallback: DiffUtil.ItemCallback<ProductUI>() {

    override fun areItemsTheSame(oldItem: ProductUI, newItem: ProductUI): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductUI, newItem: ProductUI): Boolean {
        if (oldItem.name != newItem.name) return false
        if (oldItem.detailPicture != newItem.detailPicture) return false
        if (oldItem.oldPrice != newItem.oldPrice) return false
        if (oldItem.newPrice != newItem.newPrice) return false
        if (oldItem.rating != newItem.rating) return false
        if (oldItem.status != newItem.status) return false
        if (oldItem.statusColor != newItem.statusColor) return false

        return true
    }

}