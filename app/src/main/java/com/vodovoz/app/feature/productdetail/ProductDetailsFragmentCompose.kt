package com.vodovoz.app.feature.productdetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
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
import com.vodovoz.app.core.navigation.navigateToAnalogs
import com.vodovoz.app.core.navigation.navigateToCategoryProductList
import com.vodovoz.app.core.navigation.navigateToPreOrder
import com.vodovoz.app.core.navigation.navigateToProductComments
import com.vodovoz.app.core.navigation.navigateToProductDetails
import com.vodovoz.app.core.navigation.navigateToSearch
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.EmptyResultPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.EmptyResultPlaceholderItem
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
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

                    when (viewState.uiState) {
                        ProductDetailsFlowViewModel.UiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        ProductDetailsFlowViewModel.UiState.ProductNotFound -> {
                            EmptyResultPlaceholder(
                                title = stringResource(R.string.product_not_found),
                                description = stringResource(R.string.product_not_found_details),
                                item = EmptyResultPlaceholderItem.Arrow,
                                onItemClick = { viewModel.navigateBack() }
                            )
                        }

                        ProductDetailsFlowViewModel.UiState.Success -> {
                            ProductDetailsScreen(
                                viewState = viewState,
                                viewModel = viewModel,
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
                    findNavController().navigateToPreOrder(event.id)
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
                    findNavController().navigateToProductComments(event.productId)
                }

                is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToProductAnalogs -> {
                    findNavController().navigateToAnalogs(event.productId)
                }

                ProductDetailsFlowViewModel.ProductDetailsEvents.GoBack -> {
                    findNavController().popBackStack()
                }

                is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToSearch -> {
                    findNavController().navigateToSearch(event.query)
                }

                is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToProductDetails -> {
                    findNavController().navigateToProductDetails(event.productId)
                }

                is ProductDetailsFlowViewModel.ProductDetailsEvents.GoToCategoryProductList -> {
                    findNavController().navigateToCategoryProductList(event.categoryId)
                }

                is ProductDetailsFlowViewModel.ProductDetailsEvents.Share -> {
                    kotlin.runCatching { shareText(event.text) }
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
