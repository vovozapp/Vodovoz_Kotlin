package com.vodovoz.app.feature.all.promotions

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAllPromotionsBinding
import com.vodovoz.app.feature.all.AllAdapterController
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.feature.home.banneradvinfo.BannerAdvInfoBottomSheetFragment
import com.vodovoz.app.feature.home.viewholders.homepromotions.model.PromotionAdvEntity
import com.vodovoz.app.ui.model.ListOfPromotionFilterUi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class AllPromotionsFragment : BaseFragment() {

    companion object {
        const val PROMOTION_FILTER = "PROMOTION_FILTER"
    }

    override fun layout(): Int = R.layout.fragment_all_promotions

    private val binding: FragmentAllPromotionsBinding by viewBinding {
        FragmentAllPromotionsBinding.bind(
            contentView
        )
    }

    private val viewModel: AllPromotionsFlowViewModel by viewModels()

    private val allAdapterController by lazy {
        AllAdapterController(getAllClickListener())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allAdapterController.bind(binding.rvPromotions)
        bindErrorRefresh { viewModel.refreshSorted() }
        bindSwipeRefresh()
        viewModel.updateScrollToTop()

        observeUiState()

        observeResultLiveData()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(PROMOTION_FILTER)?.observe(viewLifecycleOwner) { filterId ->
                viewModel.updateBySelectedFilter(filterId)

            }
    }

    private fun initBackButton(state: AllPromotionsFlowViewModel.AllPromotionsState) {
        val showBackButton  = findNavController().graph.id != R.id.graph_promotions

        initToolbarDropDown(state.selectedFilterUi.name, showBackBtn = showBackButton) {
            val newList = ListOfPromotionFilterUi().apply {
                addAll(state.promotionFilterUIList)
            }
            findNavController().navigate(
                AllPromotionsFragmentDirections.actionToPromotionFiltersBottomFragment(
                    state.selectedFilterUi.id,
                    newList
                )
            )
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect { state ->

                        if (state.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        initBackButton(state.data)

                        if (state.data.allPromotionBundleUI != null) {
                            allAdapterController.submitList(state.data.allPromotionBundleUI.promotionUIList)
                        }
                        if(state.data.scrollToTop){
                            binding.rvPromotions.scrollToPosition(0)
                        }


                        showError(state.error)

                    }
            }
        }
    }

    private fun getAllClickListener(): AllClickListener {
        return object : AllClickListener {
            override fun onPromotionClick(id: Long) {
                findNavController().navigate(
                    AllPromotionsFragmentDirections.actionToPromotionDetailFragment(id)
                )
            }

            override fun onPromotionAdvClick(promotionAdvEntity: PromotionAdvEntity?) {
                BannerAdvInfoBottomSheetFragment
                    .newInstance(
                        promotionAdvEntity?.titleAdv ?: "",
                        promotionAdvEntity?.bodyAdv ?: "",
                        promotionAdvEntity?.dataAdv ?: ""
                    )
                    .show(childFragmentManager, "TAG")
            }

            override fun onBrandClick(id: Long) {

            }
        }
    }

    private fun bindSwipeRefresh() {
        binding.refreshContainer.setOnRefreshListener {
            viewModel.refreshSorted()
            binding.refreshContainer.isRefreshing = false
        }
    }

    sealed class DataSource : Parcelable {
        @Parcelize
        class ByBanner(val categoryId: Long) : DataSource()

        @Parcelize
        data object All : DataSource()
    }

}
