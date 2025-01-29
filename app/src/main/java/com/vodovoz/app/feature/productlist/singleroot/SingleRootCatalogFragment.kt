package com.vodovoz.app.feature.productlist.singleroot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.speechrecognizer.SpeechDialogFragment
import com.vodovoz.app.databinding.BsCatalogSingleFlowBinding
import com.vodovoz.app.databinding.FragmentCatalogSingleFlowBinding
import com.vodovoz.app.feature.productlist.PaginatedProductsCatalogFragment
import com.vodovoz.app.feature.productlist.PaginatedProductsCatalogFragmentDirections
import com.vodovoz.app.feature.productlist.singleroot.adapter.SingleRootCatalogAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SingleRootCatalogFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_catalog_single_flow

    private val viewModel: SingleRootCatalogFlowViewModel by viewModels()

    private val binding: FragmentCatalogSingleFlowBinding by viewBinding {
        FragmentCatalogSingleFlowBinding.bind(contentView)
    }

    private val singleRootCatalogAdapter = SingleRootCatalogAdapter(
        nestingPosition = 0
    ) {
        viewModel.changeSelectedCategory(it)
    }

    @Inject
    lateinit var permissionsControllerFactory: PermissionsController.Factory
    private val permissionsController by lazy { permissionsControllerFactory.create(requireActivity()) }

    private val space: Int by lazy {
        resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchCategory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar("Категории")
        initCategoryRecycler()
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
                                state.data.bundle.categoryUIList[0].categoryUIList
                            binding.tvCategoryName.text = state.data.bundle.categoryUIList[0].name
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
}