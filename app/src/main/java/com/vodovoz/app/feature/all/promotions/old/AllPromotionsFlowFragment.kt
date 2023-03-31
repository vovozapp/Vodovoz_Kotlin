package com.vodovoz.app.feature.all.promotions.old

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.databinding.FragmentAllPromotionsBinding
import com.vodovoz.app.feature.all.AllAdapterController
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.feature.all.promotions.AllPromotionsFlowViewModel
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragmentDirections
import com.vodovoz.app.ui.model.ListOfPromotionFilterUi
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class AllPromotionsFlowFragment : BaseFragment() {

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

        observeUiState()

        observeResultLiveData()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(AllPromotionsFragment.PROMOTION_FILTER)?.observe(viewLifecycleOwner) { filterId ->
                viewModel.updateBySelectedFilter(filterId)
            }
    }

    private fun initBackButton(state: AllPromotionsFlowViewModel.AllPromotionsState) {
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
        binding.incAppBar.tvDropDownTitle.setOnClickListener {
            val newList = ListOfPromotionFilterUi().apply {
                addAll(state.promotionFilterUIList)
            }
            findNavController().navigate(
                AllPromotionsFragmentDirections.actionToPromotionFiltersBottomFragment(
                state.selectedFilterUi.id,
                newList
            ))
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->

                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    initBackButton(state.data)

                    binding.incAppBar.tvDropDownTitle.text = state.data.selectedFilterUi.name

                    if (state.data.allPromotionBundleUI != null) {
                        allAdapterController.submitList(state.data.allPromotionBundleUI.promotionUIList)
                    }

                    if (state.error !is ErrorState.Empty) {
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

            override fun onBrandClick(id: Long) {

            }
        }
    }

    sealed class DataSource : Serializable {
        class ByBanner(val categoryId: Long) : DataSource()
        class All() : DataSource()
    }

}