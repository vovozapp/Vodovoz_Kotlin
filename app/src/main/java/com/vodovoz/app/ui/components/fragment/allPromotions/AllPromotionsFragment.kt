package com.vodovoz.app.ui.components.fragment.allPromotions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.FragmentAllPromotionsBinding
import com.vodovoz.app.ui.components.adapter.allPromotionAdapter.AllPromotionMarginDecoration
import com.vodovoz.app.ui.components.adapter.allPromotionAdapter.AllPromotionsAdapter
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.PromotionDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.allProductFilters.FiltersViewModel
import com.vodovoz.app.ui.components.fragment.products.ProductsFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.PromotionFilterUI
import com.vodovoz.app.ui.model.PromotionUI
import com.vodovoz.app.ui.model.custom.AllPromotionBundleUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.Serializable

class AllPromotionsFragment : FetchStateBaseFragment() {

    companion object {
        const val PROMOTION_FILTER = "PROMOTION_FILTER"
    }

    private lateinit var binding: FragmentAllPromotionsBinding
    private lateinit var viewModel: AllPromotionsViewModel

    private val onPromotionClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val allPromotionAdapter = AllPromotionsAdapter(onPromotionClickSubject)

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    private fun getArgs() {
        viewModel.updateArgs(AllPromotionsFragmentArgs.fromBundle(requireArguments()).dataSource)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[AllPromotionsViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentAllPromotionsBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        initPromotionsRecycler()
        initAppBar()
        observeViewModel()
        observeResultLiveData()
    }

    private fun initPromotionsRecycler() {
        binding.promotionRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.promotionRecycler.adapter = allPromotionAdapter
        binding.promotionRecycler.addItemDecoration(AllPromotionMarginDecoration(
            resources.getDimension(R.dimen.primary_space).toInt())
        )
        binding.promotionRecycler.setScrollElevation(binding.appBar)
    }

    private fun initAppBar() {
        with((requireActivity() as AppCompatActivity)) {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.let { noNullActionBar ->
                title = null
                noNullActionBar.setDisplayHomeAsUpEnabled(true)
                noNullActionBar.setDisplayShowHomeEnabled(true)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.promotionFilter.setOnClickListener {
            findNavController().navigate(AllPromotionsFragmentDirections.actionAllPromotionsFragmentToPromotionFilterBottomFragment(
                viewModel.selectedFilter.id,
                viewModel.promotionFilterUIList.toTypedArray()
            ))
        }
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(PROMOTION_FILTER)?.observe(viewLifecycleOwner) { filterId ->
                viewModel.updateSelectedFilter(filterId)
            }
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Loading -> onStateLoading()
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Hide -> onStateHide()
                is FetchState.Success -> {
                    onStateSuccess()
                    fillPromotionRecycler(state.data!!.promotionUIList)
                }
            }
        }

        viewModel.selectedFilterLD.observe(viewLifecycleOwner) { filter ->
            binding.promotionFilter.text = filter.name
        }
    }

    private fun fillPromotionRecycler(promotionUIList: List<PromotionUI>) {
        val diffUtil = PromotionDiffUtilCallback(
            oldList = allPromotionAdapter.promotionUIList,
            newList = promotionUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            allPromotionAdapter.promotionUIList = promotionUIList
            diffResult.dispatchUpdatesTo(allPromotionAdapter)
        }
    }

    override fun onStart() {
        super.onStart()
        onPromotionClickSubject.subscribeBy { promotionId ->
            findNavController().navigate(
                AllPromotionsFragmentDirections.actionAllPromotionsFragmentToPromotionDetailFragment(promotionId)
            )
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    sealed class DataSource : Serializable {
        class ByBanner(val categoryId: Long) : DataSource()
        class All() : DataSource()
    }

}