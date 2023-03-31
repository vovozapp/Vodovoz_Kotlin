package com.vodovoz.app.feature.all.brands.old

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.databinding.FragmentAllBrandsBinding
import com.vodovoz.app.feature.all.AllAdapterController
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.feature.all.brands.AllBrandsFlowViewModel
import com.vodovoz.app.feature.all.brands.AllBrandsFragmentDirections
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllBrandsFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_all_brands

    private val binding: FragmentAllBrandsBinding by viewBinding {
        FragmentAllBrandsBinding.bind(
            contentView
        )
    }

    private val viewModel: AllBrandsFlowViewModel by viewModels()

    private val allAdapterController by lazy {
        AllAdapterController(getAllClickListener())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allAdapterController.bind(binding.rvBrands)

        observeUiState()
        initAppBar()
    }

    private fun initAppBar() {
        initToolbar(resources.getString(R.string.all_brands_title), true) {
            viewModel.filterByQuery(it)
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

                    allAdapterController.submitList(state.data.filteredItems)

                    if (state.error !is ErrorState.Empty) {
                        showError(state.error)
                    }

                }
        }
    }

    private fun getAllClickListener(): AllClickListener {
        return object : AllClickListener {
            override fun onPromotionClick(id: Long) {}

            override fun onBrandClick(id: Long) {
                findNavController().navigate(
                    AllBrandsFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId = id)
                    )
                )
            }
        }
    }

}