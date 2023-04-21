package com.vodovoz.app.feature.cart.ordering.intervals

import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsFreeShippingDaysBinding
import com.vodovoz.app.ui.fragment.ordering.FreeShippingSaysBSArgs
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingFreeFlowBottomSheetFragment : BaseBottomSheetFragment() {

    override fun layout(): Int {
        return R.layout.bs_free_shipping_days
    }

    private val binding: BsFreeShippingDaysBinding by viewBinding {
        BsFreeShippingDaysBinding.bind(contentView)
    }

    private val args: FreeShippingSaysBSArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.incHeader.imgClose.setOnClickListener { dismiss() }
        binding.incHeader.tvTitle.text = args.title
        binding.tvContent.text = args.info.fromHtml()
    }
}