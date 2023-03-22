package com.vodovoz.app.feature.productdetail.viewholders.detailcatandbrand

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentProductDetailsCatAndBrandBinding
import com.vodovoz.app.databinding.FragmentProductDetailsTabsBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener

class DetailCatAndBrandViewHolder(
    view: View,
    val clickListener: ProductDetailsClickListener
) : ItemViewHolder<DetailCatAndBrand>(view) {

    private val binding: FragmentProductDetailsCatAndBrandBinding = FragmentProductDetailsCatAndBrandBinding.bind(view)

    init {
        binding.clCategoryContainer.setOnClickListener {
            val itemCategoryId = item?.category?.id ?: return@setOnClickListener
            clickListener.showProductsByCategory(itemCategoryId)
        }

        binding.clBrandContainer.setOnClickListener {
            val brandId = item?.brandUI?.id ?: return@setOnClickListener
            clickListener.showProductsByBrand(brandId)
        }
    }

    override fun bind(item: DetailCatAndBrand) {
        super.bind(item)

        binding.tvCategoryName.text = item.category.name
        Glide.with(itemView.context)
            .load(item.category.detailPicture)
            .into(binding.imgCategory)

        when(item.brandUI) {
            null -> {
                binding.clBrandContainer.visibility = View.GONE
                binding.mdBetweenCategoryAndBrand.visibility = View.GONE
            }
            else -> {
                binding.clBrandContainer.visibility = View.VISIBLE
                binding.mdBetweenCategoryAndBrand.visibility = View.VISIBLE
                binding.tvBrandName.text = item.brandUI.name
                Glide.with(itemView.context)
                    .load(item.brandUI.detailPicture)
                    .into(binding.imgBrand)
            }
        }
    }

}