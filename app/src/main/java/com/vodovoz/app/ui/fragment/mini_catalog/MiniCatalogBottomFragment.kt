package com.vodovoz.app.ui.fragment.mini_catalog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsCatalogMiniBinding
import com.vodovoz.app.ui.adapter.MiniCatalogAdapter
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import io.reactivex.rxjava3.subjects.PublishSubject

class MiniCatalogBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BsCatalogMiniBinding

    private val onCategoryClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val miniCatalogAdapter = MiniCatalogAdapter(onCategoryClickSubject = onCategoryClickSubject)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BsCatalogMiniBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initDialog()
        initView()
    }.root

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = miniCatalogAdapter
        miniCatalogAdapter.categoryUIList = MiniCatalogBottomFragmentArgs.fromBundle(requireArguments()).categoryList.toList()
        miniCatalogAdapter.selectedCategoryId = MiniCatalogBottomFragmentArgs.fromBundle(requireArguments()).categoryId
        miniCatalogAdapter.notifyDataSetChanged()

        binding.incHeader.tvTitle.text = getString(R.string.categories_title)
        binding.incHeader.imgClose.setOnClickListener {
            requireDialog().cancel()
        }

        binding.btnChoose.setOnClickListener {
            findNavController().previousBackStackEntry
                ?.savedStateHandle?.set(PaginatedProductsCatalogWithoutFiltersFragment.CATEGORY_ID, miniCatalogAdapter.selectedCategoryId)
            dialog?.dismiss()
        }
    }

}