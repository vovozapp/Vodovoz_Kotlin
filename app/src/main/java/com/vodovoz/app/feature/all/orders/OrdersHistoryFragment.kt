package com.vodovoz.app.feature.all.orders

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentOrdersHistoryFlowBinding
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.ui.model.custom.OrdersFiltersBundleUI
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrdersHistoryFragment : BaseFragment() {

    companion object {
        const val FILTERS_BUNDLE =  "FILTERS_BUNDLE"
    }

    override fun layout(): Int = R.layout.fragment_orders_history_flow

    private val binding: FragmentOrdersHistoryFlowBinding by viewBinding {
        FragmentOrdersHistoryFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: AllOrdersFlowViewModel by viewModels()

    @Inject
    lateinit var accountManager: AccountManager

    @Inject
    lateinit var tabManager: TabManager

    private val allOrdersController by lazy {
        AllOrdersController(viewModel, requireContext(), getAllClickListener())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeAccount()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allOrdersController.bind(binding.rvOrders, binding.refreshContainer)
        bindErrorRefresh { viewModel.refreshSorted() }

        initFilterToolbar(true) { viewModel.goToFilter() }

        observeUiState()
        observeResultLiveData()
        bindSwipeRefresh()
        observeEvents()
    }

    private fun observeAccount() {
        lifecycleScope.launchWhenStarted {
            accountManager
                .observeAccountId()
                .collect {
                    if (it == null) {
                        tabManager.setAuthRedirect(findNavController().graph.id)
                        tabManager.selectTab(R.id.graph_profile)
                    } else {
                        viewModel.firstLoadSorted()
                    }
                }
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

                    binding.llEmptyHistoryContainer.visibility = View.GONE
                    binding.refreshContainer.visibility = View.VISIBLE

                    val data = state.data
                    if (state.bottomItem != null) {
                        allOrdersController.submitList(data.itemsList + state.bottomItem)
                    } else {
                        allOrdersController.submitList(data.itemsList)
                    }
                    if (state.error is ErrorState.Empty) {
                        binding.llEmptyHistoryContainer.visibility = View.VISIBLE
                        binding.refreshContainer.visibility = View.GONE
                    } else {
                        showError(state.error)
                    }

                }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeEvent()
                .collect {
                    when(it) {
                        is AllOrdersFlowViewModel.AllOrdersEvent.GoToFilter -> {
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.allOrdersFragment) {
                                findNavController().navigate(OrdersHistoryFragmentDirections.actionToOrdersFiltersDialog(it.bundle))
                            }
                        }
                        is AllOrdersFlowViewModel.AllOrdersEvent.GoToCart -> {
                            if (it.boolean) {
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.allOrdersFragment) {
                                    findNavController().navigate(OrdersHistoryFragmentDirections.actionToCartFragment())
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<OrdersFiltersBundleUI>(FILTERS_BUNDLE)?.observe(viewLifecycleOwner) { filtersBundle ->
                viewModel.updateFilterBundle(filtersBundle)
            }
    }

    private fun getAllClickListener(): AllClickListener {
        return object : AllClickListener {
            override fun onMoreDetailClick(orderId: Long, sendReport: Boolean) {
                //if (sendReport) accountManager.reportYandexMetrica("Зашел в заказ, статус в пути") //todo релиз
                findNavController().navigate(OrdersHistoryFragmentDirections.actionToOrderDetailsFragment(orderId))
            }

            override fun onRepeatOrderClick(orderId: Long) {
                viewModel.repeatOrder(orderId)
            }

            override fun onProductDetailPictureClick(productId: Long) {
                findNavController().navigate(OrdersHistoryFragmentDirections.actionToProductDetailFragment(productId))
            }
        }
    }

    private fun bindSwipeRefresh() {
        binding.refreshContainer.setOnRefreshListener {
            viewModel.refreshSorted()
            binding.refreshContainer.isRefreshing = false
        }
    }
}
/*
@AndroidEntryPoint
class OrdersHistoryFragment : ViewStateBaseFragment() {

    companion object {
        const val FILTERS_BUNDLE =  "FILTERS_BUNDLE"
    }

    private lateinit var binding: FragmentOrdersHistoryBinding
    private val viewModel: OrdersHistoryViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()
    private val updateSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onMoreDetailClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onRepeatOrderClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onProductDetailPictureClickSubject: PublishSubject<Long> = PublishSubject.create()

    private val pagingOrdersAdapter = PagingOrdersAdapter(
        onMoreDetailClickSubject = onMoreDetailClickSubject,
        onRepeatOrderClickSubject = onRepeatOrderClickSubject,
        onProductDetailPictureClickSubject = onProductDetailPictureClickSubject,
        orderDiffItemCallback = OrderDiffItemCallback()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeSubjects()
    }

    private fun subscribeSubjects() {
        onMoreDetailClickSubject.subscribeBy { orderId ->
            findNavController().navigate(OrdersHistoryFragmentDirections.actionToOrderDetailsFragment(orderId))
        }.addTo(compositeDisposable)
        onRepeatOrderClickSubject.subscribeBy { orderId ->
            viewModel.repeatOrder(orderId)
        }.addTo(compositeDisposable)
        onProductDetailPictureClickSubject.subscribeBy { productId ->
            findNavController().navigate(OrdersHistoryFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)
        updateSubject.subscribeBy {

        }.addTo(compositeDisposable)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentOrdersHistoryBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun update() {
        pagingOrdersAdapter.refresh()
    }

    override fun initView() {
        observeResultLiveData()
        initActionBar()
        onStateSuccess()
        initOrdersRecycler()
        subscribeData()
        observeViewModel()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<OrdersFiltersBundleUI>(FILTERS_BUNDLE)?.observe(viewLifecycleOwner) { filtersBundle ->
                viewModel.updateFilterBundle(filtersBundle)
                subscribeData()
            }
    }

    private fun initActionBar() {
        binding.bar.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.bar.filter.setOnClickListener {
            findNavController().navigate(OrdersHistoryFragmentDirections.actionToOrdersFiltersDialog(
                viewModel.ordersFiltersBundleUI
            ))
        }
    }

    private fun initOrdersRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = pagingOrdersAdapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(updateSubject),
            footer = LoadStateAdapter(updateSubject)
        )

        pagingOrdersAdapter.addLoadStateListener { state ->
            if (state.append.endOfPaginationReached) {
                if (pagingOrdersAdapter.itemCount == 0) {
                    binding.llEmptyHistoryContainer.visibility = View.VISIBLE
                    binding.rvOrders.visibility = View.INVISIBLE
                }
            }

            when (state.refresh) {
                is LoadState.Loading -> {}
                is LoadState.Error -> onStateError((state.refresh as LoadState.Error).error.message)
                is LoadState.NotLoading -> {}
            }
        }

        binding.rvOrders.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) == 0) top = space
                        bottom = space
                        left = space
                        right = space
                    }
                }
            }
        )
    }

    private fun subscribeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateOrders().collectLatest { productList ->
                pagingOrdersAdapter.submitData(productList)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> Snackbar.make(binding.root, "Неизвестная ошибка!", Snackbar.LENGTH_SHORT).show()
                is ViewState.Error -> Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.errorLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.showCartLD.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                findNavController().navigate(OrdersHistoryFragmentDirections.actionToCartFragment())
            }
        }
    }

}*/
