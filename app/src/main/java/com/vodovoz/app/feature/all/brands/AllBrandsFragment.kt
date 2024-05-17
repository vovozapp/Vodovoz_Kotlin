package com.vodovoz.app.feature.all.brands

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAllBrandsBinding
import com.vodovoz.app.feature.all.AllAdapterController
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllBrandsFragment : BaseFragment() {

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
        binding.rvBrands.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        bindErrorRefresh { viewModel.refreshSorted() }
        viewModel.updateScrollToTop()
        bindSwipeRefresh()

        observeUiState()
        initAppBar()
        initSearchBar()
    }

    private fun initSearchBar() {
        with(binding) {
            clearSearchBrand.setOnClickListener {
                editSearchBrand.setText("")
            }
            editSearchBrand.doAfterTextChanged { query ->
                clearSearchBrand.isVisible = query.toString().isNotEmpty()
                viewModel.filterByQuery(query.toString())
            }
        }
    }

    private fun initAppBar() {
        initToolbar(resources.getString(R.string.all_brands_title))
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

                        allAdapterController.submitList(state.data.filteredItems)
                        if(state.data.scrollToTop){
                            binding.rvBrands.scrollToPosition(0)
                        }

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

    private fun bindSwipeRefresh() {
        binding.refreshContainer.setOnRefreshListener {
            viewModel.refreshSorted()
            binding.refreshContainer.isRefreshing = false
        }
    }

}
