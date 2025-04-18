package com.vodovoz.app.feature.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToAllBottles
import com.vodovoz.app.core.navigation.navigateToGifts
import com.vodovoz.app.core.navigation.navigateToProductDetails
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.dialogs.VodovozDialog
import com.vodovoz.app.design_system.composables.placeholders.ErrorDataPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.cart.model.CartPresentItemUi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CartFragment : Fragment() {

    companion object {
        const val GIFT_ID = "GIFT_ID"
    }

    internal val viewModel: CartFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager


    override fun onStart() {
        super.onStart()
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.remove<CartPresentItemUi>("gift")
            ?.let { gift ->
                viewModel.addGiftToCart(gift)
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

                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState by rememberUpdatedState(pagingState.data)

                    when (val uiState = viewState.uiState) {
                        CartFlowViewModel.CartUiState.Cart -> {
                            CartScreen(viewModel = viewModel, viewState = viewState)
                        }

                        is CartFlowViewModel.CartUiState.Empty -> {
                            ErrorDataPlaceholder(
                                errorData = uiState.errorData,
                                onButtonClick = { viewModel.navigateToCatalog() }
                            )
                        }

                        CartFlowViewModel.CartUiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.fetchCartDetails() }
                        }

                        CartFlowViewModel.CartUiState.Loading -> {
                            LoadingPlaceholder()
                        }
                    }

                    LifecycleEffect {
                        viewModel.listenFavorites()
                    }

                    LifecycleEffect {
                        observeEvents()
                    }

                    if (viewState.showClearCartDialog) {
                        VodovozDialog(
                            title = stringResource(id = R.string.clear_cart_title),
                            description = stringResource(id = R.string.clear_cart_description),
                            acceptButtonText = stringResource(id = R.string.clear_cart_accept_text).uppercase(),
                            cancelButtonText = stringResource(id = R.string.clear_cart_cancel_text).uppercase(),
                            onDismiss = {
                                viewModel.closeClearCartDialog()
                            },
                            onAccept = {
                                viewModel.clearCart()
                            }
                        )
                    }

                    val currentRemoveItem = viewState.currentRemoveItem
                    if (viewState.showRemoveItemDialog && currentRemoveItem != null) {
                        VodovozDialog(
                            title = stringResource(id = R.string.delete_item_title),
                            description = stringResource(id = R.string.delete_item_description),
                            acceptButtonText = stringResource(id = R.string.delete_item_accept_text).uppercase(),
                            cancelButtonText = stringResource(id = R.string.delete_item_cancel_text).uppercase(),
                            onDismiss = {
                                viewModel.closeTrashDialog()
                            },
                            onAccept = {
                                viewModel.removeCartItem(currentRemoveItem)
                            }
                        )

                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeTabReselect()
        accountManager.reportEvent("Зашел в корзину")
    }

    private suspend fun observeEvents() {
        viewModel.observeEvent()
            .collect { event ->
                when (event) {
                    is CartFlowViewModel.CartEvents.NavigateToOrder -> {
                        if (event.prices != null) {
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.orderingFragment) {
                                findNavController().popBackStack()
                            }
                            findNavController().navigate(
                                CartFragmentDirections.actionToOrderingFragment(
                                    event.prices.total,
                                    event.prices.discountPrice,
                                    event.prices.deposit,
                                    event.prices.fullPrice,
                                    event.cart,
                                    event.coupon
                                )
                            )
                        }
                    }

                    is CartFlowViewModel.CartEvents.NavigateToGifts -> {
                        findNavController().navigateToGifts(event.popupWindow)
                    }

                    is CartFlowViewModel.CartEvents.NavigateToProfile -> {
                        tabManager.setAuthRedirect(findNavController().graph.id)
                        tabManager.selectTab(R.id.graph_profile)
                    }

                    is CartFlowViewModel.CartEvents.GoToPreOrder -> {
                        if (findNavController().currentBackStackEntry?.destination?.id == R.id.preOrderBS) {
                            findNavController().popBackStack()
                        }
                        findNavController().navigate(
                            CartFragmentDirections.actionToPreOrderBS(
                                event.id,
                                event.name,
                                event.detailPicture
                            )
                        )
                    }

                    is CartFlowViewModel.CartEvents.GoToProductDetails -> {
                        findNavController().navigateToProductDetails(event.productId)
                    }

                    CartFlowViewModel.CartEvents.GoToCatalog -> {
                        tabManager.selectTab(R.id.graph_catalog)
                    }

                    CartFlowViewModel.CartEvents.GoToAllBottles -> {
                        findNavController().navigateToAllBottles()
                    }
                }
            }
    }

    private fun observeTabReselect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tabManager.observeTabReselect()
                    .collect { id ->
                        if (id != TabManager.DEFAULT_STATE && id == R.id.cartFragment) {
                            tabManager.setDefaultState()
                        }
                    }
            }
        }
    }
}