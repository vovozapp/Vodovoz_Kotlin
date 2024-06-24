package com.vodovoz.app.feature.full_screen_history_slider

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentFullscreenHistorySliderBinding
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.full_screen_history_slider.adapter.HistoriesDetailStateAdapter
import com.vodovoz.app.feature.onlyproducts.ProductsCatalogFragment
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.interfaces.IOnChangeHistory
import com.vodovoz.app.ui.interfaces.IOnInvokeAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FullScreenHistoriesSliderFlowFragment : BaseFragment(),
    IOnChangeHistory, IOnInvokeAction {

    override fun layout() = R.layout.fragment_fullscreen_history_slider

    private val binding: FragmentFullscreenHistorySliderBinding by viewBinding {
        FragmentFullscreenHistorySliderBinding.bind(
            contentView
        )
    }

    private val viewModel: FullScreenHistoriesSliderFlowViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager


    private var startHistoryId: Long = 0
    private var lastIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setStyle(STYLE_NORMAL, R.style.FullScreenDialogWithoutStatusBar)
        viewModel.updateData()
        getArgs()
    }

    private fun getArgs() {
        FullScreenHistoriesSliderFlowFragmentArgs.fromBundle(requireArguments()).let { args ->
            startHistoryId = args.startHistoryId
        }
    }

    override fun initView() {
        observeViewModel()
        observeViewModelEvents()
    }

    private fun observeViewModelEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect {
                        when (it) {
                            is FullScreenHistoriesSliderFlowViewModel.HistoriesSliderEvents.GoToProfile -> {
                                tabManager.setAuthRedirect(findNavController().graph.id)
                                tabManager.selectTab(R.id.graph_profile)
                            }
                        }
                    }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.observeUiState().collect { state ->
                if (state.loadingPage) {
                    showLoaderWithBg(true)
                } else {
                    showLoaderWithBg(false)
                }

                val historyUIList = state.data.historyUIList
                if (historyUIList.isNotEmpty()) {
                    binding.vpHistories.adapter = HistoriesDetailStateAdapter(
                        fragment = this@FullScreenHistoriesSliderFlowFragment,
                        historyUIList = historyUIList
                    )
                    binding.vpHistories.currentItem =
                        historyUIList.indexOfFirst { it.id == startHistoryId }
                    lastIndex = historyUIList.indices.last
                }

                showError(state.error)
            }
        }
    }

    override fun nextHistory() {
        if (binding.vpHistories.currentItem == lastIndex) {
            findNavController().popBackStack()
        } else {
            binding.vpHistories.currentItem = binding.vpHistories.currentItem + 1
        }
    }

    override fun previousHistory() {
        binding.vpHistories.currentItem = binding.vpHistories.currentItem - 1
    }

    override fun close() {
        findNavController().popBackStack()
    }


    override fun update() {
        viewModel.updateData()
    }

    override fun onInvokeAction(actionEntity: ActionEntity) {
        actionEntity.invoke(findNavController(), requireActivity())
    }

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
        }
        navDirect?.let { navController.navigate(navDirect) }
    }
}