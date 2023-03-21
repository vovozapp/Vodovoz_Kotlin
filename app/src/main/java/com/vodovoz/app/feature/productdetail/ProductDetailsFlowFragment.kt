package com.vodovoz.app.feature.productdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import com.vodovoz.app.ui.fragment.product_details.ProductDetailsFragmentDirections
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
                findNavController().navigate(ProductDetailsFragmentDirections.actionToSelf(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                when(viewModel.isLoginAlready()) {
                    true -> findNavController().navigate(ProductDetailsFragmentDirections.actionToPreOrderBS(id, name, detailPicture))
                    false -> findNavController().navigate(ProductDetailsFragmentDirections.actionToProfileFragment())
                }
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                viewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

        }
    }

    private fun getProductsShowClickListener() : ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {}
            override fun showAllTopProducts(id: Long) {}
            override fun showAllNoveltiesProducts(id: Long) {}
            override fun showAllBottomProducts(id: Long) {}
        }
    }

    private fun getPromotionsClickListener() : PromotionsClickListener {
        return object : PromotionsClickListener {
            override fun onPromotionClick(id: Long) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToPromotionDetailFragment(id))
            }
            override fun onShowAllPromotionsClick() {}
        }
    }
}