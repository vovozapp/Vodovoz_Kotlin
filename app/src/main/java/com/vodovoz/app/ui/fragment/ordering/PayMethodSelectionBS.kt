package com.vodovoz.app.ui.fragment.ordering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsSelectionPayMethodBinding
import com.vodovoz.app.ui.adapter.PayMethodsAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class PayMethodSelectionBS : BottomSheetDialogFragment() {

    private val payMethodsAdapter = PayMethodsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = BsSelectionPayMethodBinding.inflate(
        layoutInflater,
        container,
        false
    ).apply {
        this.rvPayMethods.layoutManager = LinearLayoutManager(requireContext())
        payMethodsAdapter.setupListeners {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(OrderingFragment.SELECTED_PAY_METHOD, it.id)
            dismiss()
        }
        PayMethodSelectionBSArgs.fromBundle(requireArguments()).let { args ->
            payMethodsAdapter.updateData(args.payMethodList.toList(), args.selectedPayMethod)
        }

        this.rvPayMethods.adapter = payMethodsAdapter
        val space8 = resources.getDimension(R.dimen.space_8).toInt()
        this.rvPayMethods.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.top = space8
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space8
        }
    }.root

}