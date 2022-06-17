package com.vodovoz.app.ui.components.fragment.concreteFilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.databinding.FragmentFilterConcreteBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.filterValueAdapter.FilterValueDiffUtilCallback
import com.vodovoz.app.ui.components.adapter.filterValueAdapter.FilterValuesAdapter
import com.vodovoz.app.ui.components.fragment.allProductFilters.FiltersFragment
import com.vodovoz.app.ui.model.FilterValueUI

class ConcreteFilterFragment : FetchStateBaseFragment() {

    private lateinit var binding: FragmentFilterConcreteBinding
    private lateinit var viewModel: ConcreteFilterViewModel

    private val filterValuesAdapter = FilterValuesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentFilterConcreteBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        initActionBar()
        initFilterValueRecycler()
        initApplyButton()
        observeViewModel()
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ConcreteFilterViewModel::class.java]
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

    private fun getArgs() {
        viewModel.setArgs(
            categoryId = ConcreteFilterFragmentArgs.fromBundle(requireArguments()).categoryId,
            filter = ConcreteFilterFragmentArgs.fromBundle(requireArguments()).filter,
        )
    }

    private fun initApplyButton() {
        binding.apply.setOnClickListener {
            viewModel.prepareFilter(filterValuesAdapter.filterValueList)
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                FiltersFragment.CONCRETE_FILTER,
                viewModel.filter
            )
            findNavController().popBackStack()
        }
    }

    private fun initFilterValueRecycler() {
        binding.filterValueRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.filterValueRecycler.adapter = filterValuesAdapter
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { fetchState ->
            when (fetchState) {
                is FetchState.Loading -> onStateLoading()
                is FetchState.Error -> onStateError(fetchState.errorMessage)
                is FetchState.Success -> {
                    onStateSuccess()
                    fetchState.data?.let {
                        fillFilterValueList(it.filterValueList)
                        binding.toolbar.title = it.filterUI.name
                    }
                }
            }
        }
    }

    private fun fillFilterValueList(filterValueList: List<FilterValueUI>) {
        val diffUtil = FilterValueDiffUtilCallback(
            oldList = filterValuesAdapter.filterValueList,
            newList = filterValueList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            filterValuesAdapter.filterValueList = filterValueList
            diffResult.dispatchUpdatesTo(filterValuesAdapter)
        }
    }
}