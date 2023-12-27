package com.vodovoz.app.feature.productlist.singleroot

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsCatalogSingleFlowBinding
import com.vodovoz.app.feature.productlist.PaginatedProductsCatalogFragment
import com.vodovoz.app.feature.productlist.singleroot.adapter.SingleRootCatalogAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SingleRootCatalogBottomFragment : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.bs_catalog_single_flow

    private val viewModel: SingleRootCatalogFlowViewModel by viewModels()

    private val binding: BsCatalogSingleFlowBinding by viewBinding {
        BsCatalogSingleFlowBinding.bind(contentView)
    }

    private val singleRootCatalogAdapter = SingleRootCatalogAdapter(
        nestingPosition = 0
    ) {
        viewModel.changeSelectedCategory(it)
    }

    private val space: Int by lazy {
        resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchCategory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCategoryRecycler()
        initCloseButton()
        initApplyButton()
        observeUiState()
        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeEvent()
                    .collect {
                        when (it) {
                            is SingleRootCatalogFlowViewModel.SingleRootEvents.NavigateToCatalog -> {
                                findNavController().previousBackStackEntry
                                    ?.savedStateHandle?.set(
                                        PaginatedProductsCatalogFragment.CATEGORY_ID,
                                        it.id
                                    )
                                dialog?.dismiss()
                            }
                        }
                    }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeUiState()
                    .collect { state ->
                        if (state.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        if (state.data.bundle != null) {
                            singleRootCatalogAdapter.categoryUIList =
                                state.data.bundle.categoryUIList
                            singleRootCatalogAdapter.way = state.data.bundle.way
                            singleRootCatalogAdapter.notifyDataSetChanged()
                        }

                        showError(state.error)
                    }
            }
        }
    }

    private fun initCategoryRecycler() {
        val lastItemSpace = resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = singleRootCatalogAdapter
        binding.rvCategories.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom =
                lastItemSpace
        }
    }

    private fun initCloseButton() {
        binding.imgClose.setOnClickListener {
            dialog?.cancel()
        }
    }

    private fun initApplyButton() {
        binding.btnChoose.setOnClickListener {
            viewModel.navigateToCatalog()
        }
    }
}