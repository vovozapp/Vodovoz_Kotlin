package com.vodovoz.app.ui.fragment.concrete_filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentFilterConcreteBinding
import com.vodovoz.app.ui.adapter.ProductFilterValuesAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.diffUtils.FilterValueDiffUtilCallback
import com.vodovoz.app.ui.fragment.product_filters.ProductFiltersFragment
import com.vodovoz.app.ui.model.FilterValueUI

class ConcreteFilterFragment : ViewStateBaseDialogFragment() {

    private lateinit var binding: FragmentFilterConcreteBinding
    private lateinit var viewModel: ConcreteFilterViewModel

    private val productFilterValuesAdapter = ProductFilterValuesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ConcreteFilterViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.updateArgs(
            categoryId = ConcreteFilterFragmentArgs.fromBundle(requireArguments()).categoryId,
            filter = ConcreteFilterFragmentArgs.fromBundle(requireArguments()).filter,
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
        (requireActivity() as AppCompatActivity).let { appCompatActivity ->
            appCompatActivity.setSupportActionBar(binding.toolbar)
            appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initApplyButton() {
        binding.apply.setOnClickListener {
            viewModel.prepareFilter(productFilterValuesAdapter.filterValueList)
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                ProductFiltersFragment.CONCRETE_FILTER,
                viewModel.filter
            )
            findNavController().popBackStack()
        }
    }

    private fun initFilterValueRecycler() {
        binding.filterValueRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.filterValueRecycler.adapter = productFilterValuesAdapter
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
            binding.toolbar.title = concreteFilterBundleUI.filterUI.name
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