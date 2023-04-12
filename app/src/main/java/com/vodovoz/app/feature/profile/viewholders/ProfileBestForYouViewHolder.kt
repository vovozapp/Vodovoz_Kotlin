package com.vodovoz.app.feature.profile.viewholders

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.ItemProfileBestForYouBinding
import com.vodovoz.app.feature.favorite.bestforyouadapter.BestForYouAdapter
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.profile.viewholders.models.ProfileBestForYou
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig

class ProfileBestForYouViewHolder(
    view: View,
    cartManager: CartManager,
    likeManager: LikeManager,
    productsShowAllListener: ProductsShowAllListener,
    productsClickListener: ProductsClickListener
) : ItemViewHolder<ProfileBestForYou>(view) {

    private val binding: ItemProfileBestForYouBinding = ItemProfileBestForYouBinding.bind(view)

    private val bestForYouAdapter =
        BestForYouAdapter(cartManager, likeManager, productsClickListener, productsShowAllListener)

    init {
        with(binding.bestForYouProductsRecycler) {
            adapter = bestForYouAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun bind(item: ProfileBestForYou) {
        super.bind(item)

        val homeProducts = HomeProducts(
            1,
            listOf(item.data),
            ProductsSliderConfig(
                containShowAllButton = false,
                largeTitle = true
            ),
            HomeProducts.DISCOUNT
        )

        bestForYouAdapter.submitList(listOf(homeProducts))
    }

}