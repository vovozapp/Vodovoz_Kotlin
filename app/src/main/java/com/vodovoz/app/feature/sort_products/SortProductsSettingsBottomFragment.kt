package com.vodovoz.app.feature.sort_products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.BsSelectionProductsSortingBinding
import com.vodovoz.app.feature.productlist.PaginatedProductsCatalogFragment
import com.vodovoz.app.feature.sort_products.adapter.ProductsSortingAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class SortProductsSettingsBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BsSelectionProductsSortingBinding

    private val productsSortingAdapter = ProductsSortingAdapter()

    private lateinit var selectedSorting: SortType

    private val sortingList = listOf(
        SortType.POPULAR, SortType.ALPHABET, SortType.INCREASE_PRICE, SortType.REDUCE_PRICE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        selectedSorting = SortType.valueOf(SortProductsSettingsBottomFragmentArgs.fromBundle(requireArguments()).sortType)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
                PaginatedProductsCatalogFragment.SORT_TYPE, sortType.name)
            dismiss()
        }
        productsSortingAdapter.updateData(
            sortingList = sortingList,
            selectedSorting = selectedSorting
        )
    }
}