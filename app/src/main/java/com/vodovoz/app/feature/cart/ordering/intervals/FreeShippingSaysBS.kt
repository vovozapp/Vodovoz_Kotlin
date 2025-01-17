package com.vodovoz.app.feature.cart.ordering.intervals

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsFreeShippingDaysBinding
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FreeShippingSaysBS : BaseBottomSheetFragment() {

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dia ->
            val dialog = dia as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if(bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state =
                    BottomSheetBehavior.STATE_HALF_EXPANDED
                BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
                BottomSheetBehavior.from(bottomSheet).isHideable = true
            }
        }
        return bottomSheetDialog
    }
}
