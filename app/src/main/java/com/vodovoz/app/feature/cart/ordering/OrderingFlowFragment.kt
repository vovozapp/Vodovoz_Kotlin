package com.vodovoz.app.feature.cart.ordering

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentOrderingFlowBinding
import com.vodovoz.app.ui.fragment.ordering.OrderingFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class OrderingFlowFragment : BaseFragment() {

    companion object {
        const val SELECTED_PAY_METHOD = "SELECTED_PAY_METHOD"
        const val SELECTED_SHIPPING_INTERVAL = "SELECTED_SHIPPING_INTERVAL"
        const val SELECTED_SHIPPING_ALERT = "SELECTED_SHIPPING_ALERT"
    }

    override fun layout(): Int = R.layout.fragment_ordering_flow

    private val binding: FragmentOrderingFlowBinding by viewBinding {
        FragmentOrderingFlowBinding.bind(contentView)
    }

    private val args: OrderingFragmentArgs by navArgs()

    private val viewModel: OrderingFlowViewModel by viewModels()

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(getString(R.string.ordering_title_text))


    }
}