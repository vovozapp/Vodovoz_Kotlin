package com.vodovoz.app.feature.cart.ordering.intervals

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsSelectionPayMethodBinding
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsClickListener
import com.vodovoz.app.feature.cart.ordering.intervals.adapter.IntervalsController
import com.vodovoz.app.ui.fragment.ordering.OrderingFragment
import com.vodovoz.app.ui.fragment.ordering.PayMethodSelectionBSArgs
import com.vodovoz.app.ui.model.PayMethodUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayMethodFlowBottomSheetFragment : BaseBottomSheetFragment() {

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