package com.vodovoz.app.feature.addresses.add

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsAddAddressBinding
import com.vodovoz.app.ui.fragment.bottom_dialog_add_address.AddAddressViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddressFlowFragment : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.bs_add_address

    private val binding: BsAddAddressBinding by viewBinding {
        BsAddAddressBinding.bind(
            contentView
        )
    }

    private val viewModel: AddAddressFlowViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}