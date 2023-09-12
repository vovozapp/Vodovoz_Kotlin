package com.vodovoz.app.feature.filters.concrete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentFilterConcreteBinding
import com.vodovoz.app.feature.filters.concrete.adapter.ProductFilterValuesAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.diffUtils.FilterValueDiffUtilCallback
import com.vodovoz.app.feature.filters.product.ProductFiltersFragment
import com.vodovoz.app.ui.model.FilterValueUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConcreteFilterFragment : ViewStateBaseDialogFragment() {

    private lateinit var binding: FragmentFilterConcreteBinding
    private val viewModel: ConcreteFilterViewModel by viewModels()

    private val productFilterValuesAdapter = ProductFilterValuesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        getArgs()
    }

    private fun getArgs() {
        viewModel.updateArgs(
            categoryId = ConcreteFilterFlowFragmentArgs.fromBundle(requireArguments()).categoryId,
            filter = ConcreteFilterFlowFragmentArgs.fromBundle(requireArguments()).filter,
        )
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentFilterConcreteBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initActionBar()
        initFilterValueRecycler()
        initApplyButton()
        observeViewModel()
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun initActionBar() {
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initApplyButton() {
        binding.tvApply.setOnClickListener {
            viewModel.prepareFilter(productFilterValuesAdapter.filterValueList)
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                ProductFiltersFragment.CONCRETE_FILTER,
                viewModel.filter
            )
            findNavController().popBackStack()
        }
    }

    private fun initFilterValueRecycler() {
        binding.rvFilterValues.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFilterValues.adapter = productFilterValuesAdapter
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { fetchState ->
            when (fetchState) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(fetchState.errorMessage)
                is ViewState.Success -> onStateSuccess()
                is ViewState.Hide -> onStateHide()
            }
        }

        viewModel.concreteFilterBundleUILD.observe(viewLifecycleOwner) { concreteFilterBundleUI ->
            fillFilterValueList(concreteFilterBundleUI.filterValueList)
            binding.incAppBar.tvTitle.text = concreteFilterBundleUI.filterUI.name
        }
    }

    private fun fillFilterValueList(filterValueList: List<FilterValueUI>) {
        val diffUtil = FilterValueDiffUtilCallback(
            oldList = productFilterValuesAdapter.filterValueList,
            newList = filterValueList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            productFilterValuesAdapter.filterValueList = filterValueList
            diffResult.dispatchUpdatesTo(productFilterValuesAdapter)
        }
    }

}