package com.vodovoz.app.feature.productdetail

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentProductDetailsFlowBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.product_details.ProductDetailsFragmentArgs
import com.vodovoz.app.ui.fragment.product_details.ProductDetailsFragmentDirections
import com.vodovoz.app.ui.fragment.replacement_product.ReplacementProductsSelectionBS
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
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

    val args: ProductDetailsFragmentArgs by navArgs()

    private val productDetailsController by lazy {
        ProductDetailsController(
            listener = getProductDetailsClickListener(),
            productsClickListener = getProductsClickListener(),
            productsShowAllListener = getProductsShowClickListener(),
            promotionsClickListener = getPromotionsClickListener(),
            cartManager = cartManager,
            likeManager = likeManager,
            requireContext()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.fetchProductDetail()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()
        observeResultLiveData()
        observeFabCartState()

        productDetailsController.bind(binding.mainRv, binding.floatingAmountControllerContainer)
    }

    private fun observeFabCartState() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeUpdateFab()
                .collect{
                    updateFabQuantity(it)
                }
        }
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
                            detailState.detailTabs,
                            detailState.detailCatAndBrand,
                            detailState.detailBrandList.takeIf { it.productUiList.isNotEmpty() },
                            detailState.detailRecommendsProducts.takeIf { it?.items?.first()?.productUIList?.size != 0 },
                            detailState.detailPromotions.takeIf { it?.items?.promotionUIList?.size != 0 },
                            detailState.detailMaybeLikeProducts.takeIf { it.productUiList.isNotEmpty() },
                            detailState.detailSearchWord.takeIf { it?.searchWordList?.size != 0 },
                            detailState.detailBuyWith.takeIf { it?.items?.first()?.productUIList?.size != 0 },
                            detailState.detailComments
                        )
                    )

                    bindFab(detailState.productDetailUI)

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

            override fun onTvCommentAmount(productId: Long) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToAllCommentsByProductDialogFragment(productId))
            }

            override fun onYouTubeClick(videoCode: String) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToYouTubeVideoFragmentDialog(videoCode))
            }

            override fun onDetailPictureClick(currentItem: Int, detailPictureList: Array<String>) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToFullScreenDetailPicturesSliderFragment(
                    currentItem,
                    detailPictureList
                ))
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

            override fun showProductsByCategory(id: Long) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToPaginatedProductsCatalogFragment(id)
                )
            }

            override fun showProductsByBrand(id: Long) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(id)
                    )
                )
            }

            override fun onNextPageBrandProductsClick() {
                viewModel.nextPageBrandProducts()
            }

            override fun onNextPageMaybeLikeClick() {
                viewModel.nextPageMaybeLikeProducts()
            }

            override fun onQueryClick(query: String) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToSearchFragment(query))
            }

            override fun onSendComment(id: Long) {
                if (viewModel.isLoginAlready()) {
                    findNavController().navigate(ProductDetailsFragmentDirections.actionToSendCommentAboutProductFragment(id))
                } else {
                    findNavController().navigate(R.id.profileFragment)
                }
            }

            override fun onShowAllComments(id: Long) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToAllCommentsByProductDialogFragment(id))
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

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(ReplacementProductsSelectionBS.SELECTED_PRODUCT_ID)
            ?.observe(viewLifecycleOwner) { productId ->
                if (findNavController().currentDestination?.id == R.id.replacementProductsSelectionBS) {
                    findNavController().popBackStack()
                    findNavController().navigate(ProductDetailsFragmentDirections.actionToSelf(productId))
                }
            }
    }

    private fun updateFabQuantity(cartQuantity: Int) {
        binding.floatingAmountController.amount.text = cartQuantity.toString()
        binding.floatingAmountController.circleAmount.text = cartQuantity.toString()
        when (cartQuantity > 0) {
            true -> {
                binding.floatingAmountController.circleAmount.visibility = View.VISIBLE
            }
            false -> {
                binding.floatingAmountController.circleAmount.visibility = View.GONE
            }
        }
    }

    private fun bindFab(productDetailUI: ProductDetailUI?) {
        if (productDetailUI == null) return

        binding.tvFloatingOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        Glide.with(requireContext())
            .load(productDetailUI.detailPictureList.first())
            .into(binding.miniDetailImage)

        var haveDiscount = false
        when(productDetailUI.priceUIList.size) {
            1 -> {
                binding.tvFloatingCurrentPrice.setPriceText(productDetailUI.priceUIList.first().currentPrice)
                binding.tvFloatingOldPrice.setPriceText(productDetailUI.priceUIList.first().oldPrice)
                binding.tvFloatingPriceCondition.visibility = View.GONE
                if (productDetailUI.priceUIList.first().currentPrice <
                    productDetailUI.priceUIList.first().oldPrice) haveDiscount = true
            }
            else -> {
                val minimalPrice = productDetailUI.priceUIList.maxByOrNull { it.requiredAmount }!!
                binding.tvFloatingCurrentPrice.setMinimalPriceText(minimalPrice.currentPrice)
                binding.tvFloatingPriceCondition.setPriceCondition(minimalPrice.requiredAmount)
                binding.tvFloatingPriceCondition.visibility = View.VISIBLE
            }
        }
        when(haveDiscount) {
            true -> {
                binding.tvFloatingCurrentPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.tvFloatingOldPrice.visibility = View.VISIBLE
            }
            false -> {
                binding.tvFloatingCurrentPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
                binding.tvFloatingOldPrice.visibility = View.GONE
            }
        }

        //Cart amount
        updateFabQuantity(productDetailUI.cartQuantity)
    }
}