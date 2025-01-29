package com.vodovoz.app.feature.productdetail


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.media.MediaManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.design_system.VodovozTheme
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
import com.vodovoz.app.util.extensions.shareText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.fetchProductDetail()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val viewState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val productDetailUI = viewState.productDetailUI

                    val isFavorite by likeManager.observeLikes()
                        .mapLatest { idAndIsLike ->
                            idAndIsLike[productDetailUI?.id ?: -1] ?: false
                        }
                        .collectAsStateWithLifecycle(initialValue = false)


                    if (productDetailUI != null) {
                        ProductDetailsScreen(
                            productDetail = productDetailUI.copy(isFavorite = isFavorite),
                            category = viewState.categoryUI,
                            comments = viewState.commentsUI,
                            buyWithProductUIList = viewState.buyWithProductUIList,
                            showAllProperties = viewState.showAllProperties,
                            showDetailPreviewText = viewState.showDetailPreviewText,
                            viewedProductUIList = viewState.viewedProducts?.productUIList
                                ?: emptyList(),
                            articleNumber = viewState.articleNumber,
                            deposit = viewState.deposit,
                            searchWords = viewState.searchWords,
                            productCartQuantity = viewState.cartQuantity,
                            buttonIsLoading = viewState.buttonIsLoading,
                            hideFloatingButton = viewState.hideFloatingButton,
                            onFloatingButtonChange = { value ->
                                viewModel.changeFloatingButton(value)
                            },
                            onDetailPreviewTextShowOrHide = {
                                viewModel.showOrHideDetailText()
                            },
                            onAllPropertiesShow = {
                                viewModel.showAllProperties()
                            },
                            onProductImageClick = {
                                //todo - navigate to image
                            },
                            onNavigateBack = {
                                findNavController().navigateUp()
                            },
                            onShareClick = {
                                shareText(productDetailUI.shareUrl)
                            },
                            onLikeClick = {
                                viewModel.changeFavoriteStatus(
                                    productDetailUI.id,
                                    !isFavorite
                                )
                            },
                            onProductMinus = {
                                viewModel.changeCart(
                                    productDetailUI.id,
                                    viewState.cartQuantity - 1,
                                    viewState.cartQuantity
                                )
                            },
                            onProductPlus = {
                                viewModel.changeCart(
                                    productDetailUI.id,
                                    viewState.cartQuantity + 1,
                                    viewState.cartQuantity
                                )
                            },
                            onNavigateToCart = {
                                //todo - navigate to cart
                            },
                            onAddToCart = {
                                viewModel.changeCart(productDetailUI.id, 1, 0)
                            }
                        )
                    } else if (viewState.loadingPage) {
                        //todo - loading screen
                    } else {
                        //todo - error screen
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()
        observeResultLiveData()
        observeFabCartState()
        observeEvents()
        observeMediaManager()
        initJivoChatButton()


    }

    private fun initJivoChatButton() {
        //todo - check this method
//        binding.fabJivoSite.isVisible = JivoChatController.isActive()
//        binding.fabJivoSite.setOnClickListener {
//            findNavController().navigate(
//                ProductDetailsFragmentDirections.actionToWebViewFragment(
//                    JivoChatController.getLink(),
//                    ""
//                )
//            )
//        }
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
                        //todo - add fab
//                        productDetailFabController.updateFabQuantity(
//                            cartQuantity = it,
//                            amountTv = binding.floatingAmountController.amount,
//                            amountDeployed = binding.floatingAmountController.amountControllerDeployed,
//                        )
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
                            // todo - create custom loader
                            //showLoader()
                        } else {
                            // todo - create custom loader
                            //hideLoader()
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

                        // todo - change ui state
//                        productDetailFabController.bindFab(
//                            header = detailState.detailHeader,
//                            oldPriceTv = binding.tvFloatingOldPrice,
//                            miniDetailIv = binding.miniDetailImage,
//                            currentPriceTv = binding.tvFloatingCurrentPrice,
//                            conditionTv = binding.tvFloatingPriceCondition,
//                            amountTv = binding.floatingAmountController.amount,
//                            reduceIv = binding.floatingAmountController.reduceAmount,
//                            increaseIv = binding.floatingAmountController.increaseAmount,
//                            amountDeployed = binding.floatingAmountController.amountControllerDeployed
//                        )
//
//                        bindPresentLine(detailState.presentInfo)
//
//                        showError(detailState.error)
                    }
            }
        }
    }

    private fun bindPresentLine(presentInfo: PresentInfoData?) {
//        if (presentInfo != null) {
//            with(binding) {
//                presentLinearLayout.visibility = View.VISIBLE
//                presentInfo.bottomLine?.background?.color?.let {
//                    presentLinearLayout.setBackgroundColor(Color.parseColor(it))
//                }
//                presentInfo.bottomLine?.textColor?.color?.let {
//                    presentTextBottom.setTextColor(Color.parseColor(it))
//                }
//                presentTextBottom.text = presentInfo.text?.fromHtml()
//                binding.presentLinearLayout.setOnClickListener {
//                    viewModel.onPresentInfoClick()
//                }
//            }
//        } else {
//            binding.presentLinearLayout.visibility = View.GONE
//        }
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
                //todo - do scroll
                //binding.mainRv.scrollToPosition(position - 1)
                viewModel.nextPageBrandProducts()
            }

            override fun onNextPageMaybeLikeClick(position: Int) {
                //todo - do scroll
                //binding.mainRv.scrollToPosition(position)
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
