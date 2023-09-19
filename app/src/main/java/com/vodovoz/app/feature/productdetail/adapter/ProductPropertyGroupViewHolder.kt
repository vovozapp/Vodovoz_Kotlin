package com.vodovoz.app.feature.productdetail.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderPropertiesGroupBinding
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.model.PropertiesGroupUI
import com.vodovoz.app.ui.view.Divider

class ProductPropertyGroupViewHolder(
    private val binding: ViewHolderPropertiesGroupBinding,
    private val context: Context,
    private val space: Int
) : RecyclerView.ViewHolder(binding.root) {

    private val productPropertiesAdapter = ProductPropertiesAdapter()

    init {
        binding.rvProperties.layoutManager = LinearLayoutManager(context)
        binding.rvProperties.adapter = productPropertiesAdapter
        ContextCompat.getDrawable(itemView.context, R.drawable.bkg_gray_divider)?.let {
            binding.rvProperties.addItemDecoration(Divider(it, space/2))
        }
        binding.rvProperties.addMarginDecoration { rect, _, _, _ ->
            rect.top = space/2
            rect.bottom = space/2
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(propertiesGroupUI: PropertiesGroupUI, viewMode: ProductPropertyGroupsAdapter.ViewMode) {
        binding.tvName.text = propertiesGroupUI.name
        productPropertiesAdapter.viewMode = viewMode
        productPropertiesAdapter.propertyUIList = propertiesGroupUI.propertyUIList
        productPropertiesAdapter.notifyDataSetChanged()
    }

}