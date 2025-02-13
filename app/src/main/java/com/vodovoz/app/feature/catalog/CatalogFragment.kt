package com.vodovoz.app.feature.catalog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.speechrecognizer.SpeechDialogFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentMainCatalogFlowBinding
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.catalog.adapter.CatalogFlowAdapter
import com.vodovoz.app.feature.catalog.adapter.CatalogFlowClickListener
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CatalogFragment : BaseFragment() {

    private val binding: FragmentMainCatalogFlowBinding by viewBinding {
        FragmentMainCatalogFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: CatalogFlowViewModel by activityViewModels()

    private val adapter = CatalogFlowAdapter(
        clickListener = getCatalogFlowClickListener(),
        nestingPosition = 0
    )

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager

    override fun layout(): Int = R.layout.fragment_main_catalog_flow

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        viewModel.firstLoad()
//    }

    override fun initView() {
        initCategoryRecycler()
        observeStateUi()
        observeEvents()
        initSearch()
        bindErrorRefresh { viewModel.refresh() }
        bindSwipeRefresh()
        observeTabReselect()
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect {
                        when (it) {
                            is CatalogFlowViewModel.CatalogEvents.GoToProfile -> {
                                tabManager.setAuthRedirect(findNavController().graph.id)
                                tabManager.selectTab(R.id.graph_profile)
                            }
                        }
                    }
            }
        }
    }

    private fun bindSwipeRefresh() {
        binding.refreshContainer.setOnRefreshListener {
            viewModel.refresh()
            binding.refreshContainer.isRefreshing = false
        }
    }

    private fun initCategoryRecycler() {
        binding.categoryRecycler.adapter = adapter
    }

    private fun initSearch() {
        initSearchToolbar(
            "Поиск товара",
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) },
            { navigateToQrCodeFragment() },
            { startSpeechRecognizer() }
        )
    }

    private fun observeStateUi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect { catalogState ->

                        if (catalogState.loadingPage) {
                            showLoader()
                        }

                        adapter.submitList(catalogState.data.itemsList)

                        if (!catalogState.loadingPage) {
                            hideLoader()
                        }

                        showError(catalogState.error)
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
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount
            )

            is ActionEntity.Novelties -> CatalogFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties
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

    private fun getCatalogFlowClickListener(): CatalogFlowClickListener {
        return object : CatalogFlowClickListener {
            override fun onCategoryClick(category: CategoryUI) {
                if (category.actionEntity == null) {
                    if (category.id != null) {
                        findNavController().navigate(
                            CatalogFragmentDirections.actionToPaginatedProductsCatalogFragment(
                                category.id
                            )
                        )
                    }
                } else {
                    category.actionEntity.invoke()
                }
            }
        }
    }

    private fun observeTabReselect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tabManager.observeTabReselect()
                    .collect {
                        if (it != TabManager.DEFAULT_STATE && it == R.id.catalogFragment) {
                            binding.categoryRecycler.post {
                                binding.categoryRecycler.smoothScrollToPosition(0)
                            }
                            tabManager.setDefaultState()
                        }
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

    private fun startSpeechRecognizer() {
        permissionsController.methodRequiresRecordAudioPermission {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@methodRequiresRecordAudioPermission
            }
            SpeechDialogFragment().show(childFragmentManager, "TAG")

        }
    }

}