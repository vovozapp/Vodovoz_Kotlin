package com.vodovoz.app.ui.fragment.promotion_filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.databinding.BsSelectionPromotionFiltersBinding
import com.vodovoz.app.ui.adapter.PromotionsFiltersAdapter
import com.vodovoz.app.ui.fragment.all_promotions.AllPromotionsFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class PromotionFiltersBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BsSelectionPromotionFiltersBinding

    private val compositeDisposable = CompositeDisposable()

    private val onPromotionFilterClickSubject: PublishSubject<Long> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
            .addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        PromotionFiltersBottomFragmentArgs.fromBundle(requireArguments()).let { args ->
            binding.rvPromotionFilters.adapter = PromotionsFiltersAdapter(
                selectedFilterId = args.selectedFilterId,
                promotionFilterUIList = args.promotionFilterList,
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
        compositeDisposable.dispose()
    }

}