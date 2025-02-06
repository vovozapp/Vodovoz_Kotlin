package com.vodovoz.app.feature.full_screen_history_slider

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.white
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.onlyproducts.ProductsCatalogFragment
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.util.extensions.disableFullScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FullScreenHistoriesSliderFlowFragment : Fragment() {


    private val viewModel: FullScreenHistoriesSliderFlowViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateData()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val navController = findNavController()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                VodovozTheme {
                    val viewState by viewModel.observeUiState().collectAsStateWithLifecycle()

                    when (viewState.data.uiState) {
                        FullScreenHistoriesSliderFlowViewModel.UiState.Error -> {

                        }

                        FullScreenHistoriesSliderFlowViewModel.UiState.Loading -> {

                        }

                        FullScreenHistoriesSliderFlowViewModel.UiState.NetworkError -> {

                        }

                        FullScreenHistoriesSliderFlowViewModel.UiState.Success -> {
                            StoriesScreen(
                                viewState = viewState.data,
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        hideBottomBar()

    }

    override fun onStop() {
        super.onStop()
        showBottomBar()
    }


    private fun hideBottomBar() {
        requireActivity().enableEdgeToEdge()
        val bottomNavigationView =
            parentFragment?.parentFragment?.view?.findViewById<BottomNavigationView>(R.id.nvNavigation)
        bottomNavigationView?.visibility = View.GONE
    }

    private fun showBottomBar() {
        requireActivity().disableFullScreen()
        val bottomNavigationView =
            parentFragment?.parentFragment?.view?.findViewById<BottomNavigationView>(R.id.nvNavigation)
        bottomNavigationView?.visibility = View.VISIBLE
    }


//    private fun observeViewModelEvents() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.observeEvent().collect { events ->
//                    when (events) {
//                        is FullScreenHistoriesSliderFlowViewModel.HistoriesSliderEvents.GoToProfile -> {
//                            tabManager.setAuthRedirect(findNavController().graph.id)
//                            tabManager.selectTab(R.id.graph_profile)
//                        }
//
//                        FullScreenHistoriesSliderFlowViewModel.HistoriesSliderEvents.GoBack -> {
//                            findNavController().popBackStack()
//                        }
//
//                        is FullScreenHistoriesSliderFlowViewModel.HistoriesSliderEvents.ChangePagerIndex -> {
//
//                        }
//                    }
//                }
//            }
//        }
//    }


    private fun ActionEntity.invoke(navController: NavController, activity: FragmentActivity) {
        val navDirect = when (this) {
            is ActionEntity.Brand ->
                FullScreenHistoriesSliderFlowFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId = this.brandId)
                )

            is ActionEntity.Brands -> {
                FullScreenHistoriesSliderFlowFragmentDirections.actionToAllBrandsFragment(this.brandIdList.toLongArray())
            }

            is ActionEntity.Product ->
                FullScreenHistoriesSliderFlowFragmentDirections.actionToProductDetailFragment(this.productId)

            is ActionEntity.Products ->
                FullScreenHistoriesSliderFlowFragmentDirections.actionToProductsCatalogFragment(
                    ProductsCatalogFragment.DataSource.BannerProducts(categoryId = this.categoryId)
                )

            is ActionEntity.Promotion ->
                FullScreenHistoriesSliderFlowFragmentDirections.actionToPromotionDetailFragment(this.promotionId)

            is ActionEntity.Promotions -> FullScreenHistoriesSliderFlowFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.ByBanner(this.categoryId)
            )

            is ActionEntity.AllPromotions -> FullScreenHistoriesSliderFlowFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.All
            )

            is ActionEntity.Link -> {
                val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this.url))
                activity.startActivity(openLinkIntent)
                null
            }

            is ActionEntity.Category ->
                FullScreenHistoriesSliderFlowFragmentDirections.actionToPaginatedProductsCatalogFragment(
                    this.categoryId
                )

            is ActionEntity.Discount -> FullScreenHistoriesSliderFlowFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount
            )

            is ActionEntity.Novelties -> FullScreenHistoriesSliderFlowFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties
            )

            is ActionEntity.WaterApp -> {
                val eventParameters = "\"source\":\"stories\""
                accountManager.reportEvent("trekervodi_zapysk", eventParameters)
                FullScreenHistoriesSliderFlowFragmentDirections.actionToWaterAppFragment()
            }

            is ActionEntity.Delivery -> FullScreenHistoriesSliderFlowFragmentDirections.actionToWebViewFragment(
                ApiConfig.ABOUT_DELIVERY_URL,
                "О доставке"
            )

            is ActionEntity.Profile -> {
                viewModel.goToProfile()
                null
            }

            is ActionEntity.BuyCertificate -> {
                FullScreenHistoriesSliderFlowFragmentDirections.actionToBuyCertificateFragment()
            }

            is ActionEntity.LinkWithCookies -> {
                null
            }
        }
        navDirect?.let { navController.navigate(navDirect) }
    }
}