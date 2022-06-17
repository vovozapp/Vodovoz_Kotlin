package com.vodovoz.app.ui.components.fragment.promotionFilter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.BottomFragmentPromotionFilterBinding
import com.vodovoz.app.databinding.BottomFragmentSortSettingsBinding
import com.vodovoz.app.ui.components.adapter.promotionFilterAdapter.PromotionFiltersAdapter
import com.vodovoz.app.ui.components.fragment.allPromotions.AllPromotionsFragment
import com.vodovoz.app.ui.components.fragment.products.ProductsFragment
import com.vodovoz.app.ui.model.PromotionFilterUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionFilterBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomFragmentPromotionFilterBinding
    private lateinit var promotionFilterUIList: List<PromotionFilterUI>

    private val compositeDisposable = CompositeDisposable()

    private val onPromotionFilterClickSubject: PublishSubject<Long> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomFragmentPromotionFilterBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initView()
    }.root

    private fun initView() {
        binding.promotionFiltersRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.promotionFiltersRecycler
            .addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        PromotionFilterBottomFragmentArgs.fromBundle(requireArguments()).let { args ->
            binding.promotionFiltersRecycler.adapter = PromotionFiltersAdapter(
                selectedFilterId = args.selectedFIlterId,
                promotionFilterUIList = args.promotionFilterList.toList(),
                onPromotionFilterClickSubject = onPromotionFilterClickSubject
            )
        }
    }

    override fun onStart() {
        super.onStart()
        onPromotionFilterClickSubject.subscribeBy { filterId ->
            findNavController().previousBackStackEntry?.savedStateHandle
                ?.set(AllPromotionsFragment.PROMOTION_FILTER, filterId)
            dialog?.dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}