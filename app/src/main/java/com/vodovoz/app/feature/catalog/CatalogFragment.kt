package com.vodovoz.app.feature.catalog

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentMainCatalogBinding
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentMainCatalogFlowBinding
import com.vodovoz.app.feature.catalog.adapter.CatalogFlowAdapter
import com.vodovoz.app.feature.catalog.adapter.CatalogFlowClickListener
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatalogFragment : BaseFragment() {

    private val binding: FragmentMainCatalogFlowBinding by viewBinding { FragmentMainCatalogFlowBinding.bind(contentView) }
    private val viewModel: CatalogFlowViewModel by activityViewModels()

    private val adapter = CatalogFlowAdapter(
        clickListener = getCatalogFlowClickListener(),
        nestingPosition = 0
    )

    override fun layout(): Int = R.layout.fragment_main_catalog_flow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun initView() {
        initCategoryRecycler()
        observeViewModel()
        initSearch()
        bindErrorRefresh { viewModel.refresh() }
        bindSwipeRefresh()
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
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) }
        )
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect {catalogState ->

                    if (catalogState.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    adapter.submitList(catalogState.data.itemsList)

                    showError(catalogState.error)
                }
        }
    }


    private fun getCatalogFlowClickListener() : CatalogFlowClickListener {
        return object : CatalogFlowClickListener {
            override fun onCategoryClick(categoryId: Long) {
                findNavController().navigate(CatalogFragmentDirections.actionToPaginatedProductsCatalogFragment(categoryId))
            }
        }
    }

}