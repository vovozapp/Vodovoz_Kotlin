package com.vodovoz.app.feature.profile

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.NavigationHandler
import com.vodovoz.app.core.navigation.navigateToLogin
import com.vodovoz.app.core.navigation.navigateToUserData
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.profile.composables.UserNotFountPlaceholder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    internal val viewModel: ProfileFlowViewModel by activityViewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager

    @Inject
    lateinit var cookieManager: com.vodovoz.app.common.cookie.CookieManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeEvents()
        observeTabReselect()
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState by rememberUpdatedState(newValue = pagingState.data)

                    when (val uiState = viewState.uiState) {
                        ProfileFlowViewModel.ProfileUiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        ProfileFlowViewModel.ProfileUiState.Profile -> {
                            ProfileScreen(
                                viewModel = viewModel,
                                viewState = viewState
                            )
                        }

                        is ProfileFlowViewModel.ProfileUiState.UserNotFound -> {
                            UserNotFountPlaceholder(
                                title = uiState.title,
                                header = uiState.header,
                                description = uiState.description,
                                image = uiState.imageUrl,
                                button = uiState.button,
                                onButtonClick = { btn ->
                                    viewModel.navigateToLogin(btn)
                                }
                            )
                        }

                        ProfileFlowViewModel.ProfileUiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.fetchProfileDetails() }
                        }
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.checkLogin()
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect { events ->
                        when (events) {
                            is ProfileFlowViewModel.ProfileEvents.Logout -> {

                                flowViewModel.refresh()
                                cartFlowViewModel.refreshIdle()
                                favoriteViewModel.refreshIdle()
                            }

                            is ProfileFlowViewModel.ProfileEvents.GoToCart -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Товары добавлены в корзину")
                                    .setMessage("Перейти в корзину?")
                                    .setPositiveButton("Да") { dialog, _ ->
                                        dialog.dismiss()
                                        tabManager.selectTab(R.id.graph_cart)
                                    }
                                    .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                                    .show()
                            }

                            ProfileFlowViewModel.ProfileEvents.GoToLogin -> {
                                findNavController().navigateToLogin()
                            }

                            ProfileFlowViewModel.ProfileEvents.GoToUserData -> {
                                findNavController().navigateToUserData()
                            }

                            is ProfileFlowViewModel.ProfileEvents.GoByMenuItemId -> {
                                NavigationHandler.navigate(events.itemId, findNavController())
                            }
                        }
                    }
            }
        }
    }


    private fun observeTabReselect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tabManager.observeTabReselect()
                    .collect {
                        if (it != TabManager.DEFAULT_STATE && it == R.id.profileFragment) {
                            tabManager.setDefaultState()
                        }
                    }
            }
        }
    }

}