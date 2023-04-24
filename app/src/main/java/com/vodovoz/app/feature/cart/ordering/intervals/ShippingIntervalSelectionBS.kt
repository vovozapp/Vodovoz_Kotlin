package com.vodovoz.app.feature.cart.ordering.intervals

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsSelectionShippingIntervalsBinding
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsClickListener
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsController
import com.vodovoz.app.feature.cart.ordering.OrderingFragment
import com.vodovoz.app.ui.model.ShippingIntervalUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingIntervalSelectionBS : BaseBottomSheetFragment() {

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
/*
class ShippingIntervalSelectionBS : BottomSheetDialogFragment() {

    private val shippingIntervalsAdapter = ShippingIntervalsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = BsSelectionShippingIntervalsBinding.inflate(
        layoutInflater,
        container,
        false
    ).apply {
        this.rvIntervals.layoutManager = LinearLayoutManager(requireContext())
        shippingIntervalsAdapter.setupListeners {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(OrderingFragment.SELECTED_SHIPPING_INTERVAL, it.id)
            dismiss()
        }
        ShippingIntervalSelectionBSArgs.fromBundle(requireArguments()).let { args ->
            shippingIntervalsAdapter.updateData(args.shippingIntervalList.toList())
        }

        this.rvIntervals.adapter = shippingIntervalsAdapter
        val space8 = resources.getDimension(R.dimen.space_8).toInt()
        this.rvIntervals.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.top = space8
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space8
        }
    }.root

}*/
