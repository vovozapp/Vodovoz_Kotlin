package com.vodovoz.app.ui.fragment.ordering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsSelectionShippingIntervalsBinding
import com.vodovoz.app.ui.adapter.ShippingIntervalsAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

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

}