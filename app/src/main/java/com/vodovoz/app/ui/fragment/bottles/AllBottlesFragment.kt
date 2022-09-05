package com.vodovoz.app.ui.fragment.bottles

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentAllBottlesBinding
import com.vodovoz.app.databinding.FragmentAllBrandsBinding
import com.vodovoz.app.ui.adapter.AllBottlesAdapter
import com.vodovoz.app.ui.adapter.AllBrandsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.diffUtils.BottleDiffUtilCallback
import com.vodovoz.app.ui.diffUtils.BrandDiffUtilCallback
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.fragment.ordering.OrderingFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class AllBottlesFragment : ViewStateBaseFragment() {

    companion object {
        const val SELECTED_BOTTLE = "SELECTED_BOTTLE"
    }

    private lateinit var binding: FragmentAllBottlesBinding
    private lateinit var viewModel: AllBottlesViewModel

    private val compositeDisposable = CompositeDisposable()
    private val onBottleClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val allBottlesAdapter = AllBottlesAdapter(onBottleClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[AllBottlesViewModel::class.java]
        viewModel.updateData()
    }

    private fun subscribeSubjects() {
        onBottleClickSubject.subscribeBy { bottleId ->
            viewModel.addBottleToCart(bottleId)
        }.addTo(compositeDisposable)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentAllBottlesBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initAppBar()
        initBottlesRecycler()
        observeViewModel()
    }

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = "Возвратная тара"
        binding.incAppBar.imgBack.setOnClickListener {
            when(binding.incAppBar.llSearchContainer.visibility == View.VISIBLE) {
                true -> {
                    binding.incAppBar.llTitleContainer.visibility = View.VISIBLE
                    binding.incAppBar.llSearchContainer.visibility = View.GONE
                    binding.incAppBar.etSearch.setText("")
                }
                false -> findNavController().popBackStack()
            }
        }
        binding.incAppBar.imgSearch.setOnClickListener {
            binding.incAppBar.llTitleContainer.visibility = View.GONE
            binding.incAppBar.llSearchContainer.visibility = View.VISIBLE
        }
        binding.incAppBar.imgClear.setOnClickListener { binding.incAppBar.etSearch.setText("") }
        binding.incAppBar.etSearch.doAfterTextChanged { query ->
            when(query.toString().isEmpty()) {
                true -> binding.incAppBar.imgClear.visibility = View.GONE
                false -> binding.incAppBar.imgClear.visibility = View.VISIBLE
            }

            allBottlesAdapter.filter(query.toString())
        }
    }

    private fun initBottlesRecycler() {
        binding.rvBottles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBottles.adapter = allBottlesAdapter
        binding.rvBottles.setScrollElevation(binding.incAppBar.root)
        binding.rvBottles.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    override fun update() { viewModel.updateData() }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.addBottleCompletedLD.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }

        viewModel.bottleUIListLD.observe(viewLifecycleOwner) { bottleUIList ->
            val diffUtil = BottleDiffUtilCallback(
                oldList = allBottlesAdapter.bottleUIFullList,
                newList = bottleUIList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                allBottlesAdapter.bottleUIFullList = bottleUIList
                diffResult.dispatchUpdatesTo(allBottlesAdapter)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}