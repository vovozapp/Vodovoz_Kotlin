package com.vodovoz.app.ui.fragment.orders_history

import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentOrdersHistoryBinding
import com.vodovoz.app.ui.adapter.PagingOrdersAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.loadStateAdapter.LoadStateAdapter
import com.vodovoz.app.ui.diffUtils.OrderDiffItemCallback
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.custom.OrdersFiltersBundleUI
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.orders_history_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.filters -> {
                findNavController().navigate(OrdersHistoryFragmentDirections.actionToOrdersFiltersDialog(
                    viewModel.ordersFiltersBundleUI
                ))
            }
        }
        return false
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
        (requireActivity() as AppCompatActivity).let { appCompatActivity ->
            appCompatActivity.setSupportActionBar(binding.tbToolbar)
            appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.tbToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initOrdersRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvOrders.setScrollElevation(binding.apAppBar)
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

}