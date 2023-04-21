package com.vodovoz.app.ui.fragment.ordering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsSelectionPayMethodBinding
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsClickListener
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsController
import com.vodovoz.app.ui.adapter.PayMethodsAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.model.PayMethodUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayMethodSelectionBS : BaseBottomSheetFragment() {

    override fun layout(): Int {
        return R.layout.bs_selection_pay_method
    }

    private val binding: BsSelectionPayMethodBinding by viewBinding {
        BsSelectionPayMethodBinding.bind(contentView)
    }

    private val args: PayMethodSelectionBSArgs by navArgs()

    private val intervalsController by lazy {
        IntervalsController(getIntervalsClickListener(), requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = args.payMethodList.toList().map {
            it.copy(
                isSelected = it.id == args.selectedPayMethod
            )
        }

        intervalsController.bind(binding.rvPayMethods, list)
    }

    private fun getIntervalsClickListener() : IntervalsClickListener {
        return object: IntervalsClickListener {
            override fun onPayMethodClick(item: PayMethodUI) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(OrderingFragment.SELECTED_PAY_METHOD, item.id)
                dismiss()
            }
        }
    }
}
/*
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

}*/
