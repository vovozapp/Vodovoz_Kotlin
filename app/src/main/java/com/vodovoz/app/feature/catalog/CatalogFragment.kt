package com.vodovoz.app.feature.catalog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
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
import dagger.hilt.android.AndroidEntryPoint
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

    override fun layout(): Int = R.layout.fragment_main_catalog_flow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

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
        lifecycleScope.launchWhenStarted {
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

    private fun bindSwipeRefresh() {
        binding.refreshContainer.setOnRefreshListener {
            viewModel.refresh()
            binding.refreshContainer.isRefreshing = false
        }
    }

    private fun initCategoryRecycler() {
        binding.categoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecycler.adapter = adapter
    }

    private fun initSearch() {
        initSearchToolbar(
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) },
            { navigateToQrCodeFragment() },
            { startSpeechRecognizer() }
        )
    }

    private fun observeStateUi() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { catalogState ->

                    if (catalogState.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    adapter.submitList(catalogState.data.itemsList)
                    val topCatalogBanner = catalogState.data.topCatalogBanner
                    with(binding) {
                        if (topCatalogBanner != null) {


                            val backGroundColor = Color.parseColor(topCatalogBanner.backgroundColor)
                            cvCatalogBanner.backgroundTintList = ColorStateList.valueOf(
                                backGroundColor
                            )

                            val textColor = Color.parseColor(topCatalogBanner.textColor)
                            tvCatalogBanner.setTextColor(textColor)
                            tvCatalogBanner.text = topCatalogBanner.text

                            if (topCatalogBanner.iconUrl != null && topCatalogBanner.iconUrl.isNotEmpty()) {
                                Glide.with(requireContext())
                                    .load(topCatalogBanner.iconUrl)
                                    .into(iconCatalogBanner)
                            } else {
                                iconCatalogBanner.visibility = View.GONE
                            }

                            cvCatalogBanner.setOnClickListener() {
                                topCatalogBanner.actionEntity?.invoke()
                            }

                            cvCatalogBanner.visibility = View.VISIBLE
                        } else {
                            cvCatalogBanner.visibility = View.GONE
                        }
                    }

                    showError(catalogState.error)
                }
        }
    }

    private fun ActionEntity?.invoke(
        navController: NavController = findNavController(),
        activity: FragmentActivity = requireActivity(),
    ) {
        val navDirect = when (this) {
            is ActionEntity.AllPromotions -> CatalogFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.All()
            )
            is ActionEntity.Link -> {
                val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this.url))
                activity.startActivity(openLinkIntent)
                null
            }
            is ActionEntity.Discount -> CatalogFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
            )
            is ActionEntity.Novelties -> CatalogFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
            )
            is ActionEntity.WaterApp -> {
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
            else -> {
                null
            }
        }
        navDirect?.let { navController.navigate(navDirect) }
    }

    private fun getCatalogFlowClickListener(): CatalogFlowClickListener {
        return object : CatalogFlowClickListener {
            override fun onCategoryClick(categoryId: Long) {
                findNavController().navigate(
                    CatalogFragmentDirections.actionToPaginatedProductsCatalogFragment(
                        categoryId
                    )
                )
            }
        }
    }

    private fun observeTabReselect() {
        lifecycleScope.launchWhenStarted {
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

    private val permissionsController by lazy {
        PermissionsController(requireContext())
    }

    private fun navigateToQrCodeFragment() {
        permissionsController.methodRequiresCameraPermission(requireActivity()) {
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
        permissionsController.methodRequiresRecordAudioPermission(requireActivity()) {
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