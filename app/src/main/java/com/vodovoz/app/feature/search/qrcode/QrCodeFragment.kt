package com.vodovoz.app.feature.search.qrcode

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentQrCodeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrCodeFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_qr_code

    private val binding: FragmentQrCodeBinding by viewBinding {
        FragmentQrCodeBinding.bind(
            contentView
        )
    }

    private val viewModel: QrCodeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}