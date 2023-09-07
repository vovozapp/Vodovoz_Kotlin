package com.vodovoz.app.feature.cart.bottles

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAllBottlesBinding
import com.vodovoz.app.feature.cart.bottles.adapter.AllBottlesFlowAdapter
import com.vodovoz.app.feature.cart.bottles.adapter.OnBottleClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.BottleUI
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AllBottlesFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_all_bottles

    private val binding: FragmentAllBottlesBinding by viewBinding {
        FragmentAllBottlesBinding.bind(
            contentView
        )
    }

    private val viewModel: AllBottlesFlowViewModel by viewModels()

    private val allBottlesAdapter = AllBottlesFlowAdapter(getOnBottleClickListener())

    private var allBottlesWithoutFilter: List<BottleUI>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateData()
    }

    override fun initView() {
        initAppBar()
        initBottlesRecycler()
        observeViewModel()
    }

    private fun initAppBar() {
        with(binding.incAppBar) {
            tvTitle.text = "Возвратная тара"
            imgBack.setOnClickListener {
                when (llSearchContainer.visibility == View.VISIBLE) {
                    true -> {
                        llTitleContainer.visibility = View.VISIBLE
                        llSearchContainer.visibility = View.GONE
                        etSearch.setText("")
                        binding.rvBottles.scrollToPosition(0)
                        val imm =
                            getSystemService(requireContext(), InputMethodManager::class.java)
                        imm?.hideSoftInputFromWindow(etSearch.windowToken, 0)
                        etSearch.clearFocus()
                    }
                    false -> findNavController().popBackStack()
                }
            }
            imgSearch.setOnClickListener {
                llTitleContainer.visibility = View.GONE
                llSearchContainer.visibility = View.VISIBLE
                etSearch.requestFocus()
                val imm =
                    getSystemService(requireContext(), InputMethodManager::class.java)
                imm?.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT)
            }
            imgClear.setOnClickListener {
                etSearch.setText("")
                binding.rvBottles.scrollToPosition(0)
            }
            etSearch.doAfterTextChanged { query ->
                when (query.toString().isEmpty()) {
                    true -> imgClear.visibility = View.GONE
                    false -> imgClear.visibility = View.VISIBLE
                }
                filterBottlesAndSubmit()

            }
        }
    }

    private fun initBottlesRecycler() {
        binding.rvBottles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBottles.adapter = allBottlesAdapter
        binding.rvBottles.setScrollElevation(binding.incAppBar.root)
        binding.rvBottles.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->
                    if (state.loadingPage) {
                        showLoaderWithBg(true)
                    } else {
                        showLoaderWithBg(false)
                    }

                    if (state.data.itemsList.isNotEmpty()) {
                        allBottlesWithoutFilter = state.data.itemsList
                        filterBottlesAndSubmit()
                    }

                    showError(state.error)
                }
        }

        viewModel.addBottleCompletedLD.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }
    }

    private fun filterBottlesAndSubmit() {
        val query = binding.incAppBar.etSearch.text.toString()
        val filteredBottles =
            allBottlesWithoutFilter?.filter { it.name.contains(query, true) } ?: emptyList()
        allBottlesAdapter.submitList(filteredBottles)
    }

    private fun getOnBottleClickListener(): OnBottleClickListener {
        return object : OnBottleClickListener {
            override fun onBottleClick(bottle: BottleUI) {
                viewModel.addBottleToCart(bottle.id)
            }
        }
    }
}