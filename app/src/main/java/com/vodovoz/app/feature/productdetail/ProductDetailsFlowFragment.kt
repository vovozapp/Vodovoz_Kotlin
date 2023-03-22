package com.vodovoz.app.feature.productdetail

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentProductDetailsBinding
import com.vodovoz.app.databinding.FragmentProductDetailsFlowBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productdetail.viewholders.detailheader.DetailHeader
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.fragment.product_details.ProductDetailsFragmentArgs
import com.vodovoz.app.ui.fragment.product_details.ProductDetailsFragmentDirections
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_product_details_flow

    private val binding: FragmentProductDetailsFlowBinding by viewBinding { FragmentProductDetailsFlowBinding.bind(contentView) }

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
            likeManager = likeManager,
            this
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

        productDetailsController.bind(binding.mainRv, binding.floatingAmountControllerContainer)
    }

    private fun observeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { detailState ->

                    if (detailState.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }
                    productDetailsController.submitList(
                        listOfNotNull(
                            detailState.detailHeader,
                            detailState.detailPrices.takeIf { it?.priceUiList?.size != 1 },
                            detailState.detailServices.takeIf { it?.items?.size != 0 },
                            detailState.detailTabs
                        )
                    )

                    showError(detailState.error)
                }
        }
    }

    private fun getProductDetailsClickListener(): ProductDetailsClickListener {
        return object : ProductDetailsClickListener {
            override fun share(intent: Intent) {
                startActivity(intent)
            }

            override fun backPress() {
                findNavController().popBackStack()
            }

            override fun navigateToReplacement(
                detailPicture: String,
                products: Array<ProductUI>,
                id: Long,
                name: String
            ) {
                ProductDetailsFragmentDirections.actionToReplacementProductsSelectionBS(
                    detailPicture, products, id, name
                )
            }

            override fun showFabBasket() {
                binding.floatingAmountController.add.setBackgroundResource(R.drawable.bkg_button_green_circle_normal)
                binding.floatingAmountController.add.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_cart))
            }

            override fun showFabBell() {
                binding.floatingAmountController.add.setBackgroundResource(R.drawable.bkg_button_gray_circle_normal)
                binding.floatingAmountController.add.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_alert))
            }

            override fun showFabReplace() {
                binding.floatingAmountController.add.setBackgroundResource(R.drawable.bkg_button_orange_circle_normal)
                binding.floatingAmountController.add.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_swap))
            }
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