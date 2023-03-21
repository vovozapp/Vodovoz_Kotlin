package com.vodovoz.app.feature.productdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentProductDetailsBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.ui.fragment.product_details.ProductDetailsFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_product_details

    private val binding: FragmentProductDetailsBinding by viewBinding { FragmentProductDetailsBinding.bind(contentView) }

    private val viewModel: ProductDetailsFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    private val productDetailsController by lazy {
        ProductDetailsController(
            listener = getProductDetailsClickListener(),
            productsClickListener = getProductsClickListener(),
            productsShowAllListener = getProductsShowClickListener(),
            promotionsClickListener = getPromotionsClickListener(),
            cartManager = cartManager,
            likeManager = likeManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && savedInstanceState == null) {
            val productId = ProductDetailsFragmentArgs.fromBundle(requireArguments()).productId
            if (productId != 0L) {
                viewModel.fetchProductDetail(productId)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { detailState ->

                }
        }
    }

    private fun getProductDetailsClickListener(): ProductDetailsClickListener {
        return object : ProductDetailsClickListener {

        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                TODO("Not yet implemented")
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                TODO("Not yet implemented")
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                TODO("Not yet implemented")
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun getProductsShowClickListener() : ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {
                TODO("Not yet implemented")
            }

            override fun showAllTopProducts(id: Long) {
                TODO("Not yet implemented")
            }

            override fun showAllNoveltiesProducts(id: Long) {
                TODO("Not yet implemented")
            }

            override fun showAllBottomProducts(id: Long) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun getPromotionsClickListener() : PromotionsClickListener {
        return object : PromotionsClickListener {
            override fun onPromotionClick(id: Long) {
                TODO("Not yet implemented")
            }

            override fun onShowAllPromotionsClick() {
                TODO("Not yet implemented")
            }

        }
    }
}