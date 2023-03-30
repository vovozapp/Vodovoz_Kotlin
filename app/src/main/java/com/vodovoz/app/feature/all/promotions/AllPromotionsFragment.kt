package com.vodovoz.app.feature.all.promotions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.databinding.FragmentAllPromotionsBinding
import com.vodovoz.app.feature.all.AllAdapterController
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.ui.model.ListOfPromotionFilterUi
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class AllPromotionsFragment : BaseFragment() {

    companion object {
        const val PROMOTION_FILTER = "PROMOTION_FILTER"
    }

    override fun layout(): Int = R.layout.fragment_all_promotions

    private val binding: FragmentAllPromotionsBinding by viewBinding {
        FragmentAllPromotionsBinding.bind(
            contentView
        )
    }

    private val viewModel: AllPromotionsFlowViewModel by viewModels()

    private val allAdapterController by lazy {
        AllAdapterController(getAllClickListener())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allAdapterController.bind(binding.rvPromotions)

        observeUiState()

        observeResultLiveData()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(PROMOTION_FILTER)?.observe(viewLifecycleOwner) { filterId ->
                viewModel.updateBySelectedFilter(filterId)
            }
    }

    private fun initBackButton(state: AllPromotionsFlowViewModel.AllPromotionsState) {
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
        binding.incAppBar.tvDropDownTitle.setOnClickListener {
            val newList = ListOfPromotionFilterUi().apply {
                addAll(state.promotionFilterUIList)
            }
            findNavController().navigate(
                AllPromotionsFragmentDirections.actionToPromotionFiltersBottomFragment(
                    state.selectedFilterUi.id,
                    newList
                ))
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->

                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    initBackButton(state.data)

                    binding.incAppBar.tvDropDownTitle.text = state.data.selectedFilterUi.name

                    if (state.data.allPromotionBundleUI != null) {
                        allAdapterController.submitList(state.data.allPromotionBundleUI.promotionUIList)
                    }

                    if (state.error !is ErrorState.Empty) {
                        showError(state.error)
                    }

                }
        }
    }

    private fun getAllClickListener(): AllClickListener {
        return object : AllClickListener {
            override fun onPromotionClick(id: Long) {
                findNavController().navigate(
                    AllPromotionsFragmentDirections.actionToPromotionDetailFragment(id)
                )
            }

            override fun onBrandClick(id: Long) {

            }
        }
    }

    sealed class DataSource : Serializable {
        class ByBanner(val categoryId: Long) : DataSource()
        class All() : DataSource()
    }

}
/*
@AndroidEntryPoint
class AllPromotionsFragment : ViewStateBaseFragment() {

    companion object {
        const val PROMOTION_FILTER = "PROMOTION_FILTER"
    }

    private lateinit var binding: FragmentAllPromotionsBinding
    private val viewModel: AllPromotionsViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()

    private val onPromotionClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val allPromotionsAdapter = AllPromotionsAdapter(onPromotionClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        viewModel.updateArgs(AllPromotionsFragmentArgs.fromBundle(requireArguments()).dataSource)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentAllPromotionsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initPromotionsRecycler()
        initAppBar()
        observeViewModel()
        observeResultLiveData()
    }

    private fun initPromotionsRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvPromotions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPromotions.adapter = allPromotionsAdapter
        binding.rvPromotions.setScrollElevation(binding.incAppBar.apAppBar)
        binding.rvPromotions.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) == 0) top = space/2
                        bottom = space/2
                        left = space/2
                        right = space/2
                    }
                }
            }
        )
    }

    private fun initAppBar() {
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
        binding.incAppBar.tvDropDownTitle.setOnClickListener {
            findNavController().navigate(AllPromotionsFragmentDirections.actionToPromotionFiltersBottomFragment(
                viewModel.selectedFilter.id,
                viewModel.promotionFilterUIList as ListOfPromotionFilterUi
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
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.promotionBundleUILD.observe(viewLifecycleOwner) { promotionBundleUI ->
            fillPromotionRecycler(promotionBundleUI.promotionUIList)
        }

        viewModel.selectedFilterLD.observe(viewLifecycleOwner) { filter ->
            binding.incAppBar.tvDropDownTitle.text = filter.name
        }
    }

    private fun fillPromotionRecycler(promotionUIList: List<PromotionUI>) {
        val diffUtil = PromotionDiffUtilCallback(
            oldList = allPromotionsAdapter.promotionUIList,
            newList = promotionUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            allPromotionsAdapter.promotionUIList = promotionUIList
            diffResult.dispatchUpdatesTo(allPromotionsAdapter)
        }
    }

    override fun onStart() {
        super.onStart()

        onPromotionClickSubject.subscribeBy { promotionId ->
            findNavController().navigate(
                AllPromotionsFragmentDirections.actionToPromotionDetailFragment(promotionId)
            )
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

    sealed class DataSource : Serializable {
        class ByBanner(val categoryId: Long) : DataSource()
        class All() : DataSource()
    }

}*/
