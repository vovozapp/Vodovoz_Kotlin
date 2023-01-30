package com.vodovoz.app.ui.fragment.single_root_catalog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsCatalogSingleRootBinding
import com.vodovoz.app.ui.adapter.SingleRootCatalogAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseBottomFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.fragment.paginated_products_catalog.PaginatedProductsCatalogFragment
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@AndroidEntryPoint
class SingleRootCatalogBottomFragment : ViewStateBaseBottomFragment() {

    private lateinit var binding: BsCatalogSingleRootBinding
    private val viewModel: SingleRootCatalogViewModel by viewModels()

    private val categoryClickSubject: PublishSubject<CategoryUI> = PublishSubject.create()

    private val singleRootCatalogAdapter = SingleRootCatalogAdapter(
        categoryClickSubject = categoryClickSubject,
        nestingPosition = 0
    )

    private val space: Int by lazy { resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt() }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        viewModel.selectedCategoryId = SingleRootCatalogBottomFragmentArgs.fromBundle(requireArguments()).categoryId
        viewModel.fetchCategory()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = BsCatalogSingleRootBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        initDialog()
        initCategoryRecycler()
        initCloseButton()
        observeViewModel()
        observeOnCategoryClick()
        initApplyButton()
    }

    override fun update() {}

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
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

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { fetchState ->
            when (fetchState) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(fetchState.errorMessage)
                is ViewState.Success -> onStateSuccess()
                is ViewState.Hide -> onStateHide()
            }
        }

        viewModel.singleRootCatalogBundleUILD.observe(viewLifecycleOwner) { singleRootCatalogBundleUI ->
            singleRootCatalogAdapter.categoryUIList = singleRootCatalogBundleUI.categoryUIList
            singleRootCatalogAdapter.way = singleRootCatalogBundleUI.way
            singleRootCatalogAdapter.notifyDataSetChanged()
        }
    }

    private fun observeOnCategoryClick() {
        categoryClickSubject.subscribeBy { category ->
            viewModel.changeSelectedCategory(category)
        }.addTo(compositeDisposable)
    }

    private fun initApplyButton() {
        binding.btnChoose.setOnClickListener {
            findNavController().previousBackStackEntry
                ?.savedStateHandle?.set(PaginatedProductsCatalogFragment.CATEGORY_ID, viewModel.selectedCategoryId)
            dialog?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}