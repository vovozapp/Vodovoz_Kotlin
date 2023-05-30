package com.vodovoz.app.feature.all.orders.detail.traceorder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentTraceOrderBinding
import com.vodovoz.app.databinding.FragmentTraceOrderBottomBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TraceOrderFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_trace_order

    private val binding: FragmentTraceOrderBinding by viewBinding {
        FragmentTraceOrderBinding.bind(contentView)
    }

    private val viewModel: TraceOrderViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar("Где мой заказ?")



    }
}