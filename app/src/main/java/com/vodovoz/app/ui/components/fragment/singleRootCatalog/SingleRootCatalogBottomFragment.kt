package com.vodovoz.app.ui.components.fragment.singleRootCatalog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BottomFragmentCatalogMiniBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseBottomFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.singleRootCatalogAdapter.SingleRootCatalogAdapter
import com.vodovoz.app.ui.components.adapter.singleRootCatalogAdapter.SingleRootCatalogMarginDecoration
import com.vodovoz.app.ui.components.fragment.products.ProductsFragment
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class SingleRootCatalogBottomFragment : FetchStateBaseBottomFragment() {

    private lateinit var binding: BottomFragmentCatalogMiniBinding
    private lateinit var viewModel: SIngleRootCatalogViewModel

    private val categoryClickSubject: PublishSubject<CategoryUI> = PublishSubject.create()

    private val SIngleRootCatalogAdapter = SingleRootCatalogAdapter(
        categoryClickSubject = categoryClickSubject,
        nestingPosition = 0
    )

    private val space: Int by lazy { resources.getDimension(R.dimen.margin_bottom_button).toInt() }
    private val verticalMarginItemDecoration: SingleRootCatalogMarginDecoration by lazy {
        SingleRootCatalogMarginDecoration(space)
    }

    private val compositeDisposable = CompositeDisposable()

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = BottomFragmentCatalogMiniBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        initDialog()
        initViewModel()
        getArgs()
        initCategoryRecycler()
        initCloseButton()
        observeViewModel()
        observeOnCategoryClick()
        initApplyButton()
    }

    override fun update() {}

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[SIngleRootCatalogViewModel::class.java]
    }

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun getArgs() {
        viewModel.selectedCategoryId = SingleRootCatalogBottomFragmentArgs.fromBundle(requireArguments()).categoryId
        viewModel.fetchCategory()
    }

    private fun initCategoryRecycler() {
        binding.categoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecycler.adapter = SIngleRootCatalogAdapter
        binding.categoryRecycler.addItemDecoration(verticalMarginItemDecoration)
        binding.categoryRecycler.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    binding.appBar.elevation =
                        if (binding.categoryRecycler.canScrollVertically(-1)) 16f
                        else 0f
                }
            }
        )
    }

    private fun initCloseButton() {
        binding.close.setOnClickListener {
            dialog?.cancel()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { fetchState ->
            when (fetchState) {
                is FetchState.Loading -> onStateLoading()
                is FetchState.Error -> onStateError(fetchState.errorMessage)
                is FetchState.Success -> {
                    onStateSuccess()
                    fetchState.data?.let {
                        SIngleRootCatalogAdapter.categoryUIList = it.categoryUIList
                        SIngleRootCatalogAdapter.way = it.way
                        SIngleRootCatalogAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeOnCategoryClick() {
        categoryClickSubject.subscribeBy { category ->
            viewModel.changeSelectedCategory(category)
        }.addTo(compositeDisposable)
    }

    private fun initApplyButton() {
        binding.apply.setOnClickListener {
            findNavController().previousBackStackEntry
                ?.savedStateHandle?.set(ProductsFragment.CATEGORY_ID, viewModel.selectedCategoryId)
            dialog?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}