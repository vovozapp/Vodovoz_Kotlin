package com.vodovoz.app.feature.addresses

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAddressesFlowBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressesFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_addresses_flow

    private val binding: FragmentAddressesFlowBinding by viewBinding {
        FragmentAddressesFlowBinding.bind(
            contentView
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(resources.getString(R.string.addresses_title))
    }



}