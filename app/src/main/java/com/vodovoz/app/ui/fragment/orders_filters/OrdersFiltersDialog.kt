package com.vodovoz.app.ui.fragment.orders_filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.DialogFragmentOrdersFiltersBinding
import com.vodovoz.app.ui.adapter.OrderStatusesAdapter
import com.vodovoz.app.ui.fragment.orders_history.OrdersHistoryFragment
import com.vodovoz.app.ui.model.OrderStatusUI
import com.vodovoz.app.ui.model.custom.OrdersFiltersBundleUI

class OrdersFiltersDialog : DialogFragment() {

    private lateinit var ordersFiltersBundleUI: OrdersFiltersBundleUI
    private lateinit var binding: DialogFragmentOrdersFiltersBinding

    private val orderStatusUIList = listOf(
        OrderStatusUI.IN_PROCESSING,
        OrderStatusUI.IN_DELIVERY,
        OrderStatusUI.COMPLETED,
        OrderStatusUI.ACCEPTED,
        OrderStatusUI.CANCELED,
        OrderStatusUI.DELIVERED,
    )

    private lateinit var orderStatusesAdapter: OrderStatusesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        getArgs()
    }

    private fun getArgs() {
        ordersFiltersBundleUI = OrdersFiltersDialogArgs.fromBundle(requireArguments()).ordersFiltersBundleUI
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogFragmentOrdersFiltersBinding.inflate(
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
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initSearchOrderId() {
        ordersFiltersBundleUI.orderId?.let { binding.orderId.setText(ordersFiltersBundleUI.orderId.toString()) }
    }

    private fun initOrderStatusesRecycler() {
        ordersFiltersBundleUI.orderStatusUIList.forEach { orderStatusUI ->
            orderStatusUIList.find { it == orderStatusUI }?.let { it.isChecked = true }
        }
        orderStatusesAdapter = OrderStatusesAdapter().also { it.orderStatusUIList = this.orderStatusUIList }
        binding.orderStatusesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.orderStatusesRecycler.adapter = orderStatusesAdapter
    }

    private fun initButtons() {
        binding.apply.setOnClickListener {
            ordersFiltersBundleUI = OrdersFiltersBundleUI()
            orderStatusUIList.forEach { orderStatusUI ->
                if (orderStatusUI.isChecked) {
                    ordersFiltersBundleUI.orderStatusUIList.add(orderStatusUI)
                }
            }
            ordersFiltersBundleUI.orderId = binding.orderId.text.toString().toLongOrNull()
            sentOrderFiltersBundleUIBack(ordersFiltersBundleUI)
        }
        binding.clear.setOnClickListener {
            ordersFiltersBundleUI = OrdersFiltersBundleUI()
            sentOrderFiltersBundleUIBack(ordersFiltersBundleUI)
        }
    }

    private fun sentOrderFiltersBundleUIBack(ordersFiltersBundleUI: OrdersFiltersBundleUI) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            OrdersHistoryFragment.FILTERS_BUNDLE,
            ordersFiltersBundleUI
        )
        dialog?.cancel()
    }

}