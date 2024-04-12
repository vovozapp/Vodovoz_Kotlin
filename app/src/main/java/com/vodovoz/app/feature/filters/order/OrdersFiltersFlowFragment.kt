package com.vodovoz.app.feature.filters.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentOrdersFiltersBinding
import com.vodovoz.app.feature.all.orders.OrdersHistoryFragment
import com.vodovoz.app.feature.filters.order.adapter.OrderFiltersFlowAdapter
import com.vodovoz.app.ui.model.OrderFilterUI
import com.vodovoz.app.ui.model.OrderFilterUI.Companion.ALL_ID
import com.vodovoz.app.ui.model.OrderFilterUI.Companion.ALL_NAME
import com.vodovoz.app.ui.model.custom.OrdersFiltersBundleUI

class OrdersFiltersFlowFragment : Fragment() {

    private lateinit var ordersFiltersBundleUI: OrdersFiltersBundleUI
    private lateinit var binding: FragmentOrdersFiltersBinding

    private lateinit var orderStatusesAdapter: OrderFiltersFlowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        ordersFiltersBundleUI =
            OrdersFiltersFlowFragmentArgs.fromBundle(requireArguments()).ordersFiltersBundleUI
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        val anyFilter = ordersFiltersBundleUI.orderFilterUIList.firstOrNull { it.isChecked }
        ordersFiltersBundleUI.orderFilterUIList.add(
            0,
            OrderFilterUI(
                id = ALL_ID,
                name = ALL_NAME,
                isChecked = anyFilter == null
            )
        )
        orderStatusesAdapter = OrderFiltersFlowAdapter { id, checked ->
            if (id == ALL_ID ) {
                ordersFiltersBundleUI.orderFilterUIList.forEach {
                    if(id != it.id) {
                        it.isChecked = false
                    }
                }
                ordersFiltersBundleUI.orderFilterUIList.first().isChecked = true
            } else if(!checked) {
                ordersFiltersBundleUI.orderFilterUIList.first().isChecked = false
                ordersFiltersBundleUI.orderFilterUIList.firstOrNull { it.id == id }?.isChecked = true
            } else {
                ordersFiltersBundleUI.orderFilterUIList.firstOrNull { it.id == id }?.isChecked = false
                if(ordersFiltersBundleUI.orderFilterUIList.firstOrNull { it.isChecked} == null) {
                    ordersFiltersBundleUI.orderFilterUIList.first().isChecked = true
                }
            }
            orderStatusesAdapter.submitList(ordersFiltersBundleUI.orderFilterUIList)
        }
        orderStatusesAdapter.submitList(ordersFiltersBundleUI.orderFilterUIList)


        binding.rvOrderStatuses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrderStatuses.adapter = orderStatusesAdapter
    }

    private fun initButtons() {
        binding.tvApply.setOnClickListener {
            ordersFiltersBundleUI.orderFilterUIList.removeAt(0)
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