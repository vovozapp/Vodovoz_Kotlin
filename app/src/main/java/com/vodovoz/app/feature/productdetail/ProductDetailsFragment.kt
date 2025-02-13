package com.vodovoz.app.feature.productdetail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.jivochat.JivoChatController
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.media.MediaManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentProductDetailsFlowBinding
import com.vodovoz.app.feature.home.banneradvinfo.BannerAdvInfoBottomSheetFragment
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.model.PromotionAdvEntity
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productdetail.present.model.PresentInfoData
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.feature.replacement.ReplacementProductsSelectionBS
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFragment1 : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_product_details_flow

    internal val binding: FragmentProductDetailsFlowBinding by viewBinding {
        FragmentProductDetailsFlowBinding.bind(
            contentView
        )
    }

    internal val viewModel: ProductDetailsFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var mediaManager: MediaManager

    val args: ProductDetailsFragmentArgs by navArgs()

    private val productDetailsController by lazy {
        ProductDetailsController(
            listener = getProductDetailsClickListener(),
            productsClickListener = getProductsClickListener(),
            productsShowAllListener = getProductsShowClickListener(),
            promotionsClickListener = getPromotionsClickListener(),
            cartManager = cartManager,
            likeManager = likeManager,
            requireContext(),
            ratingProductManager
        )
    }

    private val productDetailFabController by lazy {
        ProductDetailFabController(
            context = requireContext(),
            viewModel = viewModel,
            navController = findNavController(),
            cartManager = cartManager,
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
        bindErrorRefresh { viewModel.fetchProductDetail() }
        observeEvents()
        observeMediaManager()
        initJivoChatButton()


        productDetailsController.bind(binding.mainRv, binding.floatingAmountControllerContainer)
    }

    private fun initJivoChatButton() {
        binding.fabJivoSite.isVisible = JivoChatController.isActive()
        binding.fabJivoSite.setOnClickListener {
            findNavController().navigate(
                ProductDetailsFragmentDirections.actionToWebViewFragment(
                    JivoChatController.getLink(),
                    ""
                )
            )
        }
    }

    private fun observeMediaManager() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mediaManager
                    .observeCommentData()
                    .collect {
                        if (it != null && it.show) {
                            mediaManager.dontShow()
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.sendCommentAboutProductFragment) {
                                findNavController().popBackStack()
                            }
                            findNavController().navigate(
                                ProductDetailsFragmentDirections.actionToSendCommentAboutProductFragment(
                                    it.productId
                                )
                            )
                        }
                    }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect {
                        when (it) {
                            is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToPreOrder -> {
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.preOrderBS) {
                                    findNavController().popBackStack()
                                }
                                findNavController().navigate(
                                    ProductDetailsFragmentDirections.actionToPreOrderBS(
                                        it.id,
                                        it.name,
                                        it.detailPicture
                                    )
                                )
                            }

                            is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToProfile -> {
                                tabManager.setAuthRedirect(findNavController().graph.id)
                                tabManager.selectTab(R.id.graph_profile)
                            }

                            is ProductDetailsFlowViewModel.ProductDetailsEvents.SendComment -> {
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.sendCommentAboutProductFragment) {
                                    findNavController().popBackStack()
                                }
                                findNavController().navigate(
                                    ProductDetailsFragmentDirections.actionToSendCommentAboutProductFragment(
                                        it.id
                                    )
                                )
                            }

                            is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToCart -> {
                                tabManager.setAuthRedirect(findNavController().graph.id)
                                tabManager.selectTab(R.id.graph_cart)
                            }

                            is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToPresentInfo -> {
                                findNavController().navigate(
                                    ProductDetailsFragmentDirections.actionProductDetailFragmentToPresentInfoBottomSheetFragment(
                                        presentText = it.presentText,
                                        progressBackground = it.progressBackground,
                                        percent = it.progress,
                                        showProgress = it.showText
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

    private fun observeFabCartState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeUpdateFab()
                    .collect {
                        productDetailFabController.updateFabQuantity(
                            cartQuantity = it,
                            amountTv = binding.floatingAmountController.amount,
                            amountDeployed = binding.floatingAmountController.amountControllerDeployed,
                        )
                    }
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                                detailState.detailBlocks.takeIf { it?.items?.size != 0 },
                                detailState.detailServices.takeIf { it?.items?.size != 0 },
                                detailState.detailTabs,
                                detailState.detailCatAndBrand,
                                detailState.detailBrandList.takeIf {
                                    it.productUiList.isNotEmpty()
                                },
                                detailState.detailBuyWithTitle.takeIf {
                                    detailState.detailBuyWith?.items?.first()?.productUIList?.size != 0
                                },
                                detailState.detailBuyWith.takeIf {
                                    it?.items?.first()?.productUIList?.size != 0
                                },
                                detailState.detailPromotionsTitle.takeIf {
                                    detailState.detailPromotions?.items?.promotionUIList?.size != 0
                                },
                                detailState.detailPromotions.takeIf {
                                    it?.items?.promotionUIList?.size != 0
                                },
                                detailState.detailRecommendsProductsTitle.takeIf {
                                    detailState.detailRecommendsProducts?.items?.first()?.productUIList?.size != 0
                                },
                                detailState.detailRecommendsProducts.takeIf {
                                    it?.items?.first()?.productUIList?.size != 0
                                },
                                detailState.detailMaybeLikeProducts.takeIf {
                                    it.productUiList.isNotEmpty()
                                },
                                detailState.detailSearchWord.takeIf {
                                    it?.searchWordList?.size != 0
                                },
                                detailState.viewedProductsTitle.takeIf {
                                    detailState.viewedProducts?.productUIList?.size != 0
                                },
                                detailState.viewedProducts.takeIf {
                                    it?.productUIList?.isNotEmpty() ?: false
                                },
                                detailState.detailComments
                            )
                        )

                        productDetailFabController.bindFab(
                            header = detailState.detailHeader,
                            oldPriceTv = binding.tvFloatingOldPrice,
                            miniDetailIv = binding.miniDetailImage,
                            currentPriceTv = binding.tvFloatingCurrentPrice,
                            conditionTv = binding.tvFloatingPriceCondition,
                            amountTv = binding.floatingAmountController.amount,
                            reduceIv = binding.floatingAmountController.reduceAmount,
                            increaseIv = binding.floatingAmountController.increaseAmount,
                            amountDeployed = binding.floatingAmountController.amountControllerDeployed
                        )

                        bindPresentLine(detailState.presentInfo)

                        showError(detailState.error)
                    }
            }
        }
    }

    private fun bindPresentLine(presentInfo: PresentInfoData?) {
        if (presentInfo != null) {
            with(binding) {
                presentLinearLayout.visibility = View.VISIBLE
                presentInfo.bottomLine?.background?.color?.let {
                    presentLinearLayout.setBackgroundColor(Color.parseColor(it))
                }
                presentInfo.bottomLine?.textColor?.color?.let {
                    presentTextBottom.setTextColor(Color.parseColor(it))
                }
                presentTextBottom.text = presentInfo.text?.fromHtml()
                binding.presentLinearLayout.setOnClickListener {
                    viewModel.onPresentInfoClick()
                }
            }
        } else {
            binding.presentLinearLayout.visibility = View.GONE
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
                name: String,
                categoryId: Long?,
            ) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToReplacementProductsSelectionBS(
                        detailPicture, products, id, name, categoryId = categoryId ?: -1L
                    )
                )
            }

            override fun onTvCommentAmount(productId: Long) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToAllCommentsByProductDialogFragment(
                        productId
                    )
                )
            }

            override fun onYouTubeClick(videoCode: String) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToYouTubeVideoFragmentDialog(
                        videoCode
                    )
                )
            }

            override fun onRuTubeClick(videoCode: String) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToRuTubeVideoFragmentDialog(videoCode)
                )
            }

            override fun onDetailPictureClick(currentItem: Int, detailPictureList: Array<String>) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToFullScreenDetailPicturesSliderFragment(
                        currentItem,
                        detailPictureList
                    )
                )
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

            override fun onNextPageBrandProductsClick(position: Int) {
                binding.mainRv.scrollToPosition(position - 1)
                viewModel.nextPageBrandProducts()
            }

            override fun onNextPageMaybeLikeClick(position: Int) {
                binding.mainRv.scrollToPosition(position)
                viewModel.nextPageMaybeLikeProducts()
            }

            override fun onQueryClick(query: String) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToSearchFragment(
                        query
                    )
                )
            }

            override fun onSendComment(id: Long) {
                viewModel.onSendCommentClick(id)
            }

            override fun onShowAllComments(id: Long) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToAllCommentsByProductDialogFragment(
                        id
                    )
                )
            }

            override fun onBlockButtonClick(productId: String, extProductId: String) {
                viewModel.changeCart(productId, extProductId)
            }

            override fun onServiceClick(id: String) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToServiceDetailFragment(
                        emptyArray(),
                        id
                    )
                )
            }
        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToSelf(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                viewModel.onPreOrderClick(id, name, detailPicture)
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                viewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

            override fun onChangeRating(id: Long, rating: Float, oldRating: Float) {
                viewModel.changeRating(id, rating, oldRating)
            }
        }
    }

    private fun getProductsShowClickListener(): ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {}
            override fun showAllTopProducts(id: Long) {}
            override fun showAllNoveltiesProducts(id: Long) {}
            override fun showAllBottomProducts(id: Long) {}
        }
    }

    private fun getPromotionsClickListener(): PromotionsClickListener {
        return object : PromotionsClickListener {
            override fun onPromotionClick(id: Long) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToPromotionDetailFragment(
                        id
                    )
                )
            }

            override fun onShowAllPromotionsClick() {}

            override fun onPromotionAdvClick(promotionAdvEntity: PromotionAdvEntity?) {
                BannerAdvInfoBottomSheetFragment
                    .newInstance(
                        promotionAdvEntity?.titleAdv ?: "",
                        promotionAdvEntity?.bodyAdv ?: "",
                        promotionAdvEntity?.dataAdv ?: ""
                    )
                    .show(childFragmentManager, "TAG")
            }
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(ReplacementProductsSelectionBS.SELECTED_PRODUCT_ID)
            ?.observe(viewLifecycleOwner) { productId ->
                if (findNavController().currentDestination?.id == R.id.replacementProductsSelectionBS) {
                    findNavController().popBackStack()
                    findNavController().navigate(
                        ProductDetailsFragmentDirections.actionToSelf(
                            productId
                        )
                    )
                }
            }
    }
}
