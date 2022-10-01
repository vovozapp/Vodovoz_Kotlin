package com.vodovoz.app.ui.fragment.ordering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsShippingAlertsSelectionBinding
import com.vodovoz.app.ui.adapter.ShippingAlertsAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class ShippingAlertsSelectionBS : BottomSheetDialogFragment() {

    private val shippingAlertsAdapter = ShippingAlertsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = BsShippingAlertsSelectionBinding.inflate(
        layoutInflater,
        container,
        false
    ).apply {
        this.incHeader.tvTitle.text = "Предупредить о приезде водителя"
        this.incHeader.imgClose.setOnClickListener { dismiss() }
        this.rvShippingAlerts.layoutManager = LinearLayoutManager(requireContext())
        shippingAlertsAdapter.setupListeners {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(OrderingFragment.SELECTED_SHIPPING_ALERT, it.id)
            dismiss()
        }
        shippingAlertsAdapter.updateData(ShippingAlertsSelectionBSArgs.fromBundle(requireArguments()).shippingAlertList.toList())
        this.rvShippingAlerts.adapter = shippingAlertsAdapter
        val space8 = resources.getDimension(R.dimen.space_8).toInt()
        this.rvShippingAlerts.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.top = space8
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space8
        }
    }.root

}