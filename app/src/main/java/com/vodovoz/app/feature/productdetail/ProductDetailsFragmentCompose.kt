package com.vodovoz.app.feature.productdetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
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
import com.vodovoz.app.design_system.composables.placeholders.ProductNotFoundPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.replacement.ReplacementProductsSelectionBS
import com.vodovoz.app.util.extensions.shareText
import dagger.hilt.android.AndroidEntryPoint
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
        viewModel.loadProductDetails(args.productId)
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
                            ProductNotFoundPlaceholder(
                                onBack = {
                                    viewModel.navigateBack()
                                },
                                haveArrow = true
                            )
                            LoadingPlaceholder()
                        }

                        ProductDetailsFlowViewModel.UiState.ProductNotFound -> {
                            ProductNotFoundPlaceholder(
                                onBack = {
                                    viewModel.navigateBack()
                                },
                                haveArrow = true
                            )
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
                                onNavigateToCart = {
                                    //todo - navigate to cart
                                },
                                onAddToCart = {
                                    viewModel.changeCart(productDetails.id, 1, 0)
                                }
                            )
                        }
                    }


                    LifecycleEffect { observeEvents() }

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeResultLiveData()
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

    private suspend fun observeEvents() {
        viewModel.observeEvent().collect { event ->
                when (event) {
                    is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToPreOrder -> {
                        findNavController().navigate(
                            R.id.preOrderFragment,
                            bundleOf("productId" to event.id),
                            NavOptions.Builder()
                                .setEnterAnim(R.anim.slide_in_botton)
                                .setExitAnim(R.anim.slide_out_botton)
                                .setPopEnterAnim(R.anim.slide_in_botton)
                                .setPopExitAnim(R.anim.slide_out_botton)
                                .build()
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
                                event.id
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
                                presentText = event.presentText,
                                progressBackground = event.progressBackground,
                                percent = event.progress,
                                showProgress = event.showText
                            )
                        )
                    }

                    ProductDetailsFlowViewModel.ProductDetailsEvents.GoToAboutProduct -> {
                        findNavController().navigate(R.id.aboutProductFragment)
                    }

                    is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToProductComments -> {
                        findNavController().navigate(
                            R.id.productCommentsFragment,
                            bundleOf("productId" to event.productId)
                        )
                    }

                    is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToProductsCollection -> {
                        findNavController().navigate(
                            R.id.productsCollectionFragment,
                            bundleOf("productId" to event.productId)
                        )
                    }

                    ProductDetailsFlowViewModel.ProductDetailsEvents.GoBack -> {
                        findNavController().popBackStack()
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
