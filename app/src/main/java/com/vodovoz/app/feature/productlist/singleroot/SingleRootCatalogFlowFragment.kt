package com.vodovoz.app.feature.productlist.singleroot

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsCatalogSingleRootBinding
import com.vodovoz.app.feature.productlist.PaginatedProductsCatalogFragment
import com.vodovoz.app.feature.productlist.singleroot.adapter.SingleRootClickListener
import com.vodovoz.app.feature.productlist.singleroot.adapter.SingleRootFlowAdapter
import com.vodovoz.app.ui.adapter.SingleRootCatalogAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.fragment.single_root_catalog.SingleRootCatalogViewModel
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SingleRootCatalogFlowFragment : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.bs_catalog_single_root

    private val viewModel: SingleRootCatalogFlowViewModel by viewModels()

    private val binding: BsCatalogSingleRootBinding by viewBinding {
        BsCatalogSingleRootBinding.bind(contentView)
    }

    private val categoryClickSubject: PublishSubject<CategoryUI> = PublishSubject.create()

    private val singleRootCatalogAdapter = SingleRootFlowAdapter(
        clickListener = getSingleRootClickListener(),
        nestingPosition = 0
    )

    private val space: Int by lazy { resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt() }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchCategory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCategoryRecycler()
        initCloseButton()
        observeOnCategoryClick()
        initApplyButton()
        observeUiState()
        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeEvent()
                .collect {
                    when(it) {
                        is SingleRootCatalogFlowViewModel.SingleRootEvents.NavigateToCatalog -> {
                            findNavController().previousBackStackEntry
                                ?.savedStateHandle?.set(PaginatedProductsCatalogFragment.CATEGORY_ID, it.id)
                            dialog?.dismiss()
                        }
                    }
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeUiState()
                .collect { state ->
                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }




                    showError(state.error)
                }
        }
    }

    private fun initCategoryRecycler() {
        val lastItemSpace = resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = singleRootCatalogAdapter
        binding.rvCategories.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = lastItemSpace
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

    private fun observeOnCategoryClick() {
        categoryClickSubject.subscribeBy { category ->
            viewModel.changeSelectedCategory(category)
        }.addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun getSingleRootClickListener() : SingleRootClickListener {
        return object : SingleRootClickListener {
            override fun onCatClick(category: CategoryUI) {
                viewModel.changeSelectedCategory(category)
            }
        }
    }
}