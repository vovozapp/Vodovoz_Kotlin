package com.vodovoz.app.feature.all.orders.detail.traceorder.bottom

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.FragmentTraceOrderBottomBinding
import com.vodovoz.app.feature.all.orders.detail.traceorder.TraceOrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TraceOrderBottomSheetFragment : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.fragment_trace_order_bottom

    private val binding: FragmentTraceOrderBottomBinding by viewBinding {
        FragmentTraceOrderBottomBinding.bind(contentView)
    }

    private val viewModel: TraceOrderViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}