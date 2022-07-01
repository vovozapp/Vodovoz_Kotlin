package com.vodovoz.app.ui.components.fragment.sort_products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.BottomFragmentSortSettingsBinding
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog.PaginatedProductsCatalogFragment

class SortProductsSettingsBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomFragmentSortSettingsBinding
    private lateinit var sortType: SortType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomFragmentSortSettingsBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        getArgs()
        initView()
    }.root

    private fun getArgs() {
        sortType = SortType.valueOf(SortProductsSettingsBottomFragmentArgs.fromBundle(requireArguments()).sortType)
    }

    private fun initView() {
        changeColorForSelected(sortType)
        binding.popularSort.setOnClickListener { selectSort(SortType.POPULAR) }
        binding.alphabeticallySort.setOnClickListener { selectSort(SortType.ALPHABET) }
        binding.reducePriceSort.setOnClickListener { selectSort(SortType.REDUCE_PRICE) }
        binding.increasePriceSort.setOnClickListener { selectSort(SortType.INCREASE_PRICE) }
    }

    private fun selectSort(sortType: SortType) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(PaginatedProductsCatalogFragment.SORT_TYPE, sortType.name)
        dialog?.dismiss()
    }

    private fun changeColorForSelected(sortType: SortType) {
        when(sortType) {
            SortType.ALPHABET -> binding.alphabeticallySort
            SortType.POPULAR -> binding.popularSort
            SortType.INCREASE_PRICE -> binding.increasePriceSort
            SortType.REDUCE_PRICE -> binding.reducePriceSort
            SortType.NO_SORT -> null
        }.also { it?.setTextColor(ContextCompat.getColor(requireContext(), R.color.bluePrimary)) }
    }

}