package com.vodovoz.app.feature.filters.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentOrdersFiltersBinding
import com.vodovoz.app.feature.filters.order.adapter.OrderStatusesAdapter
import com.vodovoz.app.feature.all.orders.OrdersHistoryFragment
import com.vodovoz.app.feature.filters.order.adapter.OrderStatusesFlowAdapter
import com.vodovoz.app.ui.model.OrderStatusUI
import com.vodovoz.app.ui.model.custom.OrdersFiltersBundleUI

class OrdersFiltersFlowFragment : Fragment() {

    private lateinit var ordersFiltersBundleUI: OrdersFiltersBundleUI
    private lateinit var binding: FragmentOrdersFiltersBinding

    private val orderStatusUIList = listOf(
        OrderStatusUI.IN_PROCESSING,
        OrderStatusUI.IN_DELIVERY,
        OrderStatusUI.COMPLETED,
        OrderStatusUI.ACCEPTED,
        OrderStatusUI.CANCELED,
        OrderStatusUI.DELIVERED,
    )

    private lateinit var orderStatusesAdapter: OrderStatusesFlowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        ordersFiltersBundleUI = OrdersFiltersFlowFragmentArgs.fromBundle(requireArguments()).ordersFiltersBundleUI
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentOrdersFiltersBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initAppBar()
        initSearchOrderId()
        initButtons()
        initOrderStatusesRecycler()
    }.root

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = resources.getString(R.string.orders_filters_title)
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initSearchOrderId() {
        ordersFiltersBundleUI.orderId?.let { binding.etOrderId.setText(ordersFiltersBundleUI.orderId.toString()) }
    }

    private fun initOrderStatusesRecycler() {
        ordersFiltersBundleUI.orderStatusUIList.forEach { orderStatusUI ->
            orderStatusUIList.find { it == orderStatusUI }?.let { it.isChecked = true }
        }
        orderStatusesAdapter = OrderStatusesFlowAdapter().also {
            it.submitList(orderStatusUIList)
        }

        binding.rvOrderStatuses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrderStatuses.adapter = orderStatusesAdapter
    }

    private fun initButtons() {
        binding.tvApply.setOnClickListener {
            ordersFiltersBundleUI = OrdersFiltersBundleUI()
            orderStatusUIList.forEach { orderStatusUI ->
                if (orderStatusUI.isChecked) {
                    ordersFiltersBundleUI.orderStatusUIList.add(orderStatusUI)
                }
            }
            ordersFiltersBundleUI.orderId = binding.etOrderId.text.toString().toLongOrNull()
            sentOrderFiltersBundleUIBack(ordersFiltersBundleUI)
        }
        binding.tvClear.setOnClickListener {
            ordersFiltersBundleUI = OrdersFiltersBundleUI()
            sentOrderFiltersBundleUIBack(ordersFiltersBundleUI)
        }
    }

    private fun sentOrderFiltersBundleUIBack(ordersFiltersBundleUI: OrdersFiltersBundleUI) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            OrdersHistoryFragment.FILTERS_BUNDLE,
            ordersFiltersBundleUI
        )
        findNavController().popBackStack()
    }

}