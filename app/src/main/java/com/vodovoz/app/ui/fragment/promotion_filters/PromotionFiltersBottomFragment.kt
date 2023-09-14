package com.vodovoz.app.ui.fragment.promotion_filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.databinding.BsSelectionPromotionFiltersBinding
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.ui.adapter.PromotionsFiltersAdapter

class PromotionFiltersBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BsSelectionPromotionFiltersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = BsSelectionPromotionFiltersBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initView()
    }.root

    private fun initView() {
        binding.rvPromotionFilters.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPromotionFilters
            .addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        PromotionFiltersBottomFragmentArgs.fromBundle(requireArguments()).let { args ->
            binding.rvPromotionFilters.adapter = PromotionsFiltersAdapter(
                selectedFilterId = args.selectedFilterId,
                promotionFilterUIList = args.promotionFilterList
            ) { filterId ->
                findNavController().previousBackStackEntry?.savedStateHandle
                    ?.set(AllPromotionsFragment.PROMOTION_FILTER, filterId)
                dialog?.dismiss()
            }
        }
    }
}