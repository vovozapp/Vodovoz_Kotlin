package com.vodovoz.app.feature.all.orders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentOrdersHistoryFlowBinding
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.ui.fragment.orders_history.OrdersHistoryFragment
import com.vodovoz.app.ui.fragment.orders_history.OrdersHistoryFragmentDirections
import com.vodovoz.app.ui.model.custom.OrdersFiltersBundleUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllOrdersFlowFragment : BaseFragment() {

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

    private val allOrdersController by lazy {
        AllOrdersController(viewModel, requireContext(), getAllClickListener())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allOrdersController.bind(binding.rvOrders, binding.refreshContainer)
        bindErrorRefresh { viewModel.refreshSorted() }

        initFilterToolbar(true) { viewModel.goToFilter() }

        observeUiState()
        observeResultLiveData()
        bindSwipeRefresh()
        observeGoToCart()
        observeGoToFilter()
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

                    val data = state.data
                    if (state.bottomItem != null) {
                        allOrdersController.submitList(data.itemsList + state.bottomItem)
                    } else {
                        allOrdersController.submitList(data.itemsList)
                    }

                    showError(state.error)

                }
        }
    }

    private fun observeGoToFilter() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeGoToFilter()
                .collect {
                    findNavController().navigate(OrdersHistoryFragmentDirections.actionToOrdersFiltersDialog(it))
                }
        }
    }

    private fun observeGoToCart() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeGoToCart()
                .collect {
                    if (it) {
                        findNavController().navigate(OrdersHistoryFragmentDirections.actionToCartFragment())
                    }
                }
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<OrdersFiltersBundleUI>(OrdersHistoryFragment.FILTERS_BUNDLE)?.observe(viewLifecycleOwner) { filtersBundle ->
                viewModel.updateFilterBundle(filtersBundle)
            }
    }

    private fun getAllClickListener(): AllClickListener {
        return object : AllClickListener {
            override fun onMoreDetailClick(orderId: Long) {
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