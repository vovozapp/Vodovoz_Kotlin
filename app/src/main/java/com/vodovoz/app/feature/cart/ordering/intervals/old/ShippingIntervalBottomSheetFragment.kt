package com.vodovoz.app.feature.cart.ordering.intervals.old

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsSelectionShippingIntervalsBinding
import com.vodovoz.app.feature.cart.ordering.intervals.ShippingIntervalSelectionBSArgs
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsClickListener
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsController
import com.vodovoz.app.ui.fragment.ordering.OrderingFragment
import com.vodovoz.app.ui.model.ShippingIntervalUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingIntervalBottomSheetFragment : BaseBottomSheetFragment() {

    override fun layout(): Int {
        return R.layout.bs_selection_shipping_intervals
    }

    private val binding: BsSelectionShippingIntervalsBinding by viewBinding {
        BsSelectionShippingIntervalsBinding.bind(contentView)
    }

    private val args: ShippingIntervalSelectionBSArgs by navArgs()

    private val intervalsController by lazy {
        IntervalsController(getIntervalsClickListener(), requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intervalsController.bind(binding.rvIntervals, args.shippingIntervalList.toList())
    }

    private fun getIntervalsClickListener() : IntervalsClickListener {
        return object: IntervalsClickListener {
            override fun onIntervalClick(item: ShippingIntervalUI) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(OrderingFragment.SELECTED_SHIPPING_INTERVAL, item.id)
                dismiss()
            }
        }
    }
}