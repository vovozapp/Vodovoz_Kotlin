package com.vodovoz.app.feature.productdetail


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
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.feature.replacement.ReplacementProductsSelectionBS
import com.vodovoz.app.util.extensions.shareText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.fetchProductDetail()
        }
    }

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
                    val productDetails = viewState.productDetails

                    when (viewState.uiState) {
                        ProductDetailsFlowViewModel.UiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        ProductDetailsFlowViewModel.UiState.ProductNotFound -> {

                        }

                        ProductDetailsFlowViewModel.UiState.Success -> {
                            ProductDetailsScreen(
                                viewState = viewState,
                                viewModel = viewModel,
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
                                    shareText(productDetails.shareUrl)
                                },
                                onLikeClick = {
                                    //todo
//                                    viewModel.changeFavoriteStatus(
//                                        productDetails.id,
//                                        true
//                                    )
                                },
                                onProductMinus = {
                                    viewModel.changeCart(
                                        productDetails.id,
                                        viewState.cartQuantity - 1,
                                        viewState.cartQuantity
                                    )
                                },
                                onProductPlus = {
                                    viewModel.changeCart(
                                        productDetails.id,
                                        viewState.cartQuantity + 1,
                                        viewState.cartQuantity
                                    )
                                },
                                onNavigateToCart = {
                                    //todo - navigate to cart
                                },
                                onAddToCart = {
                                    viewModel.changeCart(productDetails.id, 1, 0)
                                }
                            )
                        }
                    }

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeResultLiveData()
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
