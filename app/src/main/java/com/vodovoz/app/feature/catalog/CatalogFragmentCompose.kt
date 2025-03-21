package com.vodovoz.app.feature.catalog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.android.activate
import com.vodovoz.app.core.navigation.navigateToCategoryProductList
import com.vodovoz.app.core.navigation.navigateToSubCategories
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private val viewModel: CatalogFlowViewModel by activityViewModels()


    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
        observeEvents()
        observeTabReselect()
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
                    val viewState = pagingState.data

                    when (viewState.uiState) {
                        CatalogFlowViewModel.UiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.fetchCatalogDetails() }
                        }

                        else -> {
                            CatalogScreen(viewModel = viewModel, viewState = viewState)
                        }
                    }

                }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect { event ->
                        when (event) {
                            is CatalogFlowViewModel.CatalogEvents.GoToProfile -> {
                                tabManager.setAuthRedirect(findNavController().graph.id)
                                tabManager.selectTab(R.id.graph_profile)
                            }

                            CatalogFlowViewModel.CatalogEvents.GoToSearch -> {
                                findNavController().navigate(R.id.searchFragment)
                            }

                            is CatalogFlowViewModel.CatalogEvents.GoToSubCategories -> {
                                findNavController().navigateToSubCategories(event.catalogCategory)
                            }

                            is CatalogFlowViewModel.CatalogEvents.GoToProductList -> {
                                findNavController().navigateToCategoryProductList(event.catalogCategory.id)
                            }

                            is CatalogFlowViewModel.CatalogEvents.ActivateDataAllAction -> {
                                event.action.activate(findNavController())
                            }
                        }
                    }
            }
        }
    }


    private fun ActionEntity?.invoke(
        navController: NavController = findNavController(),
        activity: FragmentActivity = requireActivity(),
    ) {
        val navDirect = when (this) {
            is ActionEntity.AllPromotions -> CatalogFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.All
            )

            is ActionEntity.Link -> {
                val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this.url))
                activity.startActivity(openLinkIntent)
                null
            }

            is ActionEntity.Discount -> CatalogFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.HurryBuyUpProducts
            )

            is ActionEntity.Novelties -> CatalogFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.NewProducts
            )

            is ActionEntity.WaterApp -> {
                val eventParameters = "\"source\":\"catalog\""
                accountManager.reportEvent("trekervodi_zapysk", eventParameters)
                CatalogFragmentDirections.actionToWaterAppFragment()
            }

            is ActionEntity.Delivery -> {
                CatalogFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_DELIVERY_URL,
                    "О доставке"
                )
            }

            is ActionEntity.Profile -> {
                viewModel.goToProfile()
                null
            }

            is ActionEntity.BuyCertificate -> {
                CatalogFragmentDirections.actionToBuyCertificateFragment()
            }

            else -> {
                null
            }

        }
        navDirect?.let { navController.navigate(navDirect) }
    }

    private fun observeTabReselect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tabManager.observeTabReselect()
                    .collect {
//                        if (it != TabManager.DEFAULT_STATE && it == R.id.catalogFragment) {
//                            binding.categoryRecycler.post {
//                                binding.categoryRecycler.smoothScrollToPosition(0)
//                            }
//                            tabManager.setDefaultState()
//                        }
                    }
            }
        }
    }

    @Inject
    lateinit var permissionsControllerFactory: PermissionsController.Factory
    private val permissionsController by lazy { permissionsControllerFactory.create(requireActivity()) }

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

}