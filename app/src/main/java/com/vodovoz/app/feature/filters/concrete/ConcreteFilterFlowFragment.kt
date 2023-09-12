package com.vodovoz.app.feature.filters.concrete

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentFilterConcreteBinding
import com.vodovoz.app.feature.filters.concrete.adapter.ProductFilterValuesFlowAdapter
import com.vodovoz.app.feature.filters.product.ProductFiltersFlowFragment
import com.vodovoz.app.ui.model.FilterValueUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConcreteFilterFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_filter_concrete

    private val viewModel: ConcreteFilterFlowViewModel by viewModels()

    private val productFilterValuesAdapter = ProductFilterValuesFlowAdapter()

    private val binding: FragmentFilterConcreteBinding by viewBinding {
        FragmentFilterConcreteBinding.bind(
            contentView
        )
    }

    override fun initView() {
        initActionBar()
        initFilterValueRecycler()
        initApplyButton()
        observeViewModel()
        update()
    }

    override fun update() {
        viewModel.fetchProductFilterById()
    }

    private fun initActionBar() {
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initApplyButton() {
        binding.tvApply.setOnClickListener {
            val filterList = mutableListOf<FilterValueUI>().apply {
                repeat(productFilterValuesAdapter.itemCount) {
                    add(productFilterValuesAdapter.getItem(it) as FilterValueUI)
                }
            }
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                ProductFiltersFlowFragment.CONCRETE_FILTER,
                viewModel.prepareFilter(filterList.toList())
            )
            findNavController().popBackStack()
        }
    }

    private fun initFilterValueRecycler() {
        binding.rvFilterValues.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFilterValues.adapter = productFilterValuesAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.observeUiState().collect { state ->
                if (state.loadingPage) {
                    showLoaderWithBg(true)
                } else {
                    showLoaderWithBg(false)
                }

                val concreteFilter = state.data.concreteFilterBundleUI
                if (concreteFilter != null) {
                    productFilterValuesAdapter.submitList(concreteFilter.filterValueList)
                    binding.incAppBar.tvTitle.text = concreteFilter.filterUI.name
                }

                showError(state.error)
            }
        }
    }
}