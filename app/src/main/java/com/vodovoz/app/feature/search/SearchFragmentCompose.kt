package com.vodovoz.app.feature.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToAnalogs
import com.vodovoz.app.core.navigation.navigateToProductDetails
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    internal val viewModel: SearchFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var permissionsControllerFactory: PermissionsController.Factory
    private val permissionsController by lazy { permissionsControllerFactory.create(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeEvents()
        viewModel.firstLoad()
    }

    override fun onStart() {
        super.onStart()
        tabManager.changeTabVisibility(true)
    }


    override fun onStop() {
        super.onStop()
        tabManager.changeTabVisibility(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState = pagingState.data

                    when(viewState.uiState){
                        SearchFlowViewModel.UiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.retrySearchQuery() }
                        }
                        else ->{
                            SearchScreen(viewModel = viewModel, viewState = viewState)
                        }
                    }

                    LifecycleEffect {
                        viewModel.listenCart()
                    }

                    LifecycleEffect {
                        viewModel.listenProductLoadings()
                    }

                    LifecycleEffect {
                        viewModel.listenSearchHistory()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearScrollState()
        initBackButton()
    }

    private fun navigateToQrCodeFragment() {
        permissionsController.methodRequiresCameraPermission {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@methodRequiresCameraPermission
            }

            findNavController().navigate(R.id.qrCodeFragment)

        }
    }


    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect { event ->
                        when (event) {
                            is SearchFlowViewModel.SearchEvents.GoToPreOrder -> {
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.preOrderBS) {
                                    findNavController().popBackStack()
                                }
                                findNavController().navigate(
                                    SearchFragmentDirections.actionToPreOrderBS(
                                        event.productId,
                                        event.name,
                                        event.detailPicture
                                    )
                                )
                            }

                            is SearchFlowViewModel.SearchEvents.GoToProfile -> {
                                tabManager.setAuthRedirect(findNavController().graph.id)
                                tabManager.selectTab(R.id.graph_profile)
                            }

                            SearchFlowViewModel.SearchEvents.GoToContacts -> {
                                findNavController().navigate(SearchFragmentDirections.actionToContactsFragment())
                            }

                            SearchFlowViewModel.SearchEvents.GoToPromotions -> {
                                findNavController().navigate(
                                    SearchFragmentDirections.actionToAllPromotionsFragment(
                                        AllPromotionsFragment.DataSource.All
                                    )
                                )
                            }

                            is SearchFlowViewModel.SearchEvents.GoToService -> {
                                findNavController().navigate(
                                    SearchFragmentDirections.actionToServiceDetailNewFragment(event.id)
                                )
                            }

                            is SearchFlowViewModel.SearchEvents.GoToWebView -> {
                                findNavController().navigate(
                                    SearchFragmentDirections.actionToWebViewFragment(
                                        event.url,
                                        event.title
                                    )
                                )
                            }

                            SearchFlowViewModel.SearchEvents.GoBack -> {
                                findNavController().popBackStack()
                            }

                            is SearchFlowViewModel.SearchEvents.GoToProductList -> {
                                findNavController().navigate(
                                    R.id.paginatedProductsCatalogWithoutFiltersFragment,
                                    bundleOf("dataSource" to event.searchDataSource)
                                )
                            }

                            SearchFlowViewModel.SearchEvents.GoToScanner -> {
                                navigateToQrCodeFragment()
                            }

                            is SearchFlowViewModel.SearchEvents.GoToProductDetails -> {
                                findNavController().navigateToProductDetails(event.productId)
                            }

                            is SearchFlowViewModel.SearchEvents.GoToProductAnalogs -> {
                                findNavController().navigateToAnalogs(event.productId)
                            }
                        }
                    }
            }
        }
    }

    private fun initBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }


}
