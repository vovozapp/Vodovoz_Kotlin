package com.vodovoz.app.feature.cart.ordering.intervals.old

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsShippingAlertsSelectionBinding
import com.vodovoz.app.feature.cart.ordering.intervals.ShippingAlertsSelectionBSArgs
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsClickListener
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsController
import com.vodovoz.app.feature.cart.ordering.OrderingFragment
import com.vodovoz.app.ui.model.ShippingAlertUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingAlertBottomSheetFragment : BaseBottomSheetFragment() {

    override fun layout(): Int {
        return R.layout.bs_shipping_alerts_selection
    }

    private val binding: BsShippingAlertsSelectionBinding by viewBinding {
        BsShippingAlertsSelectionBinding.bind(contentView)
    }

    private val args: ShippingAlertsSelectionBSArgs by navArgs()

    private val intervalsController by lazy {
        IntervalsController(getIntervalsClickListener(), requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.incHeader.tvTitle.text = "Предупредить о приезде водителя"
        binding.incHeader.imgClose.setOnClickListener { dismiss() }

        intervalsController.bind(binding.rvShippingAlerts, args.shippingAlertList.toList())
    }

    private fun getIntervalsClickListener() : IntervalsClickListener {
        return object: IntervalsClickListener {
            override fun onAlertClick(item: ShippingAlertUI) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(OrderingFragment.SELECTED_SHIPPING_ALERT, item.id)
                dismiss()
            }
        }
    }
}