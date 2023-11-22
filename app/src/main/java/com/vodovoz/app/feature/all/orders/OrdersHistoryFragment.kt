package com.vodovoz.app.feature.all.orders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        const val FILTERS_BUNDLE = "FILTERS_BUNDLE"
    }

    override fun layout(): Int = R.layout.fragment_orders_history_flow

    private val binding: FragmentOrdersHistoryFlowBinding by viewBinding {
        FragmentOrdersHistoryFlowBinding.bind(
            contentView
        )
    }
    internal val viewModel: AllOrdersFlowViewModel by viewModels()

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
        observeUiState()
        observeEvents()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allOrdersController.bind(binding.rvOrders, binding.refreshContainer)

        bindErrorRefresh { viewModel.refreshSorted() }

        initFilterToolbar(true) { viewModel.goToFilter() }
        observeResultLiveData()
        bindSwipeRefresh()
    }

    private fun observeAccount() {
        lifecycleScope.launchWhenStarted {
            accountManager
                .observeAccountId()
                .collect {
                    if (it == null) {
                        findNavController().popBackStack()
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
                    when (it) {
                        is AllOrdersFlowViewModel.AllOrdersEvent.GoToFilter -> {
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.allOrdersFragment) {
                                findNavController().navigate(
                                    OrdersHistoryFragmentDirections.actionToOrdersFiltersDialog(
                                        it.bundle
                                    )
                                )
                            }
                        }
                        is AllOrdersFlowViewModel.AllOrdersEvent.GoToCart -> {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Товары добавлены в корзину")
                                .setMessage("Перейти в корзину?")
                                .setPositiveButton("Да") { dialog, _ ->
                                    dialog.dismiss()
                                    if (findNavController().currentBackStackEntry?.destination?.id == R.id.allOrdersFragment) {
                                        findNavController().navigate(
                                            OrdersHistoryFragmentDirections.actionToCartFragment()
                                        )
                                    }
                                }
                                .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                                .show()
                        }
                    }
                }
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<OrdersFiltersBundleUI>(FILTERS_BUNDLE)
            ?.observe(viewLifecycleOwner) { filtersBundle ->
                viewModel.updateFilterBundle(filtersBundle)
            }
    }

    private fun getAllClickListener(): AllClickListener {
        return object : AllClickListener {
            override fun onMoreDetailClick(orderId: Long, sendReport: Boolean) {
                if (sendReport) {
                    val eventParameters = "\"ZakazID\":\"$orderId\""
                    accountManager.reportYandexMetrica(
                        "Зашел в заказ, статус в пути",
                        eventParameters
                    )
                }
                findNavController().navigate(
                    OrdersHistoryFragmentDirections.actionToOrderDetailsFragment(
                        orderId
                    )
                )
            }

            override fun onRepeatOrderClick(orderId: Long) {
                viewModel.repeatOrder(orderId)
            }

            override fun onProductDetailPictureClick(productId: Long) {
                findNavController().navigate(
                    OrdersHistoryFragmentDirections.actionToProductDetailFragment(
                        productId
                    )
                )
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
