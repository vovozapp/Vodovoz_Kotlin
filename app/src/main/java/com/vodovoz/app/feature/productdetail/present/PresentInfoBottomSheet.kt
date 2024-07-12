package com.vodovoz.app.feature.productdetail.present

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsPresentInfoBinding
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.fromHtml

class PresentInfoBottomSheetFragment : BaseBottomSheetFragment() {

    override fun layout(): Int {
        return R.layout.bs_present_info
    }

    private val binding: BsPresentInfoBinding by viewBinding { BsPresentInfoBinding.bind(contentView) }

    private val args: PresentInfoBottomSheetFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post{
            with(binding) {
                val width = presentProgress.width * args.percent / 100
                val layoutParamsText = presentProgressText.layoutParams
                layoutParamsText.width = width
                presentProgressText.setLayoutParams(layoutParamsText)
            }
        }
        initViews()
    }

    private fun initViews() {
        with(binding) {
            presentText.text = args.presentText.fromHtml()

            presentProgress.progress = args.percent
            if(args.progressBackground.isNotEmpty()) {
                presentProgress.progressTintList =
                    ColorStateList.valueOf(Color.parseColor(args.progressBackground))
            }

            presentProgressText.isVisible = args.showProgress
            presentProgressText.text = args.percent.toString() + "%"

            presentButton.setOnClickListener {
                dismiss()
            }
        }
    }
}