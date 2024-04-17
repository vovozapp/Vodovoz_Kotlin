package com.vodovoz.app.feature.filters.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
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
import com.vodovoz.app.util.extensions.debugLog

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
        ordersFiltersBundleUI.orderId?.let {
            binding.etOrderId.setText(ordersFiltersBundleUI.orderId.toString())
        }
    }

    private fun initOrderStatusesRecycler() {
        with(ordersFiltersBundleUI.orderFilterUIList) {
            val anyFilter = firstOrNull { it.isChecked }
            if (first().id != ALL_ID) {
                add(
                    0,
                    OrderFilterUI(
                        id = ALL_ID,
                        name = ALL_NAME,
                        isChecked = anyFilter == null
                    )
                )
            }
            orderStatusesAdapter = OrderFiltersFlowAdapter { id, checked ->
                if (id == ALL_ID) {
                    forEach {
                        if (id != it.id) {
                            it.isChecked = false
                        }
                    }
                    first().isChecked = true
                } else if (!checked) {
                    first().isChecked = false
                    firstOrNull { it.id == id }?.isChecked =
                        true
                } else {
                    firstOrNull { it.id == id }?.isChecked =
                        false
                    if (firstOrNull { it.isChecked } == null) {
                        first().isChecked = true
                    }
                }
                orderStatusesAdapter.items = (this)
            }
            binding.rvOrderStatuses.layoutManager = LinearLayoutManager(requireContext())
            binding.rvOrderStatuses.adapter = orderStatusesAdapter
            orderStatusesAdapter.items = (this)
        }
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
        binding.etOrderId.doAfterTextChanged {
            debugLog { it.toString() + " " +  it.isNullOrEmpty() }
            disableEnableControls(it.isNullOrEmpty(), binding.rvOrderStatuses)
        }
    }

    private fun sentOrderFiltersBundleUIBack(ordersFiltersBundleUI: OrdersFiltersBundleUI) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            OrdersHistoryFragment.FILTERS_BUNDLE,
            ordersFiltersBundleUI
        )
        findNavController().popBackStack()
    }

    private fun disableEnableControls(enable: Boolean, vg: ViewGroup) {
        for (i in 0 until vg.childCount) {
            val child = vg.getChildAt(i)
            child.setEnabled(enable)
            if (child is ViewGroup) {
                disableEnableControls(enable, child)
            }
        }
    }

}