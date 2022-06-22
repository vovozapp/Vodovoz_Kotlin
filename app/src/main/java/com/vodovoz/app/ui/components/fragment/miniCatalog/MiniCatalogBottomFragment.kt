package com.vodovoz.app.ui.components.fragment.miniCatalog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.databinding.BottomFragmentCatalogMiniBinding
import com.vodovoz.app.ui.components.adapter.miniCatalog.MiniCatalogAdapter
import com.vodovoz.app.ui.components.adapter.singleRootCatalogAdapter.SingleRootCatalogAdapter
import com.vodovoz.app.ui.components.fragment.products.ProductsFragment
import com.vodovoz.app.ui.components.fragment.productsWithoutFilter.ProductsWithoutFiltersFragment
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class MiniCatalogBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomFragmentCatalogMiniBinding

    private val onCategoryClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val miniCatalogAdapter = MiniCatalogAdapter(onCategoryClickSubject = onCategoryClickSubject)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BottomFragmentCatalogMiniBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initDialog()
        initCategoryRecycler()
    }.root

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initCategoryRecycler() {
        binding.categoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecycler.adapter = miniCatalogAdapter
        miniCatalogAdapter.categoryUIList = MiniCatalogBottomFragmentArgs.fromBundle(requireArguments()).categoryList.toList()
        miniCatalogAdapter.selectedCategoryId = MiniCatalogBottomFragmentArgs.fromBundle(requireArguments()).categoryId
        miniCatalogAdapter.notifyDataSetChanged()

        binding.close.setOnClickListener {
            requireDialog().cancel()
        }

        binding.apply.setOnClickListener {
            findNavController().previousBackStackEntry
                ?.savedStateHandle?.set(ProductsWithoutFiltersFragment.CATEGORY_ID, miniCatalogAdapter.selectedCategoryId)
            dialog?.dismiss()
        }
    }

}