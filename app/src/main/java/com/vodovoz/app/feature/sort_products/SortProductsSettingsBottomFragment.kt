package com.vodovoz.app.feature.sort_products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsSelectionProductsSortingBinding
import com.vodovoz.app.feature.productlist.PaginatedProductsCatalogFragment
import com.vodovoz.app.feature.sort_products.adapter.ProductsSortingAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.model.SortTypeListUI
import com.vodovoz.app.ui.model.SortTypeUI

class SortProductsSettingsBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BsSelectionProductsSortingBinding

    private val productsSortingAdapter = ProductsSortingAdapter()

    private val args: SortProductsSettingsBottomFragmentArgs by navArgs()

    private lateinit var selectedSorting: SortTypeUI
    private lateinit var sortingList: SortTypeListUI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        selectedSorting = args.sortType
        sortingList = args.sortTypeList
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = BsSelectionProductsSortingBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSortingRecycler()
    }

    private fun setupSortingRecycler() {
        binding.rvSort.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSort.adapter = productsSortingAdapter
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvSort.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.top = space
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space
        }
        productsSortingAdapter.setupListeners { sortType ->
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                PaginatedProductsCatalogFragment.SORT_TYPE, sortType
            )
            dismiss()
        }
        productsSortingAdapter.updateData(
            sortingList = sortingList.sortTypeList,
            selectedSorting = selectedSorting
        )
    }
}