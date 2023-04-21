package com.vodovoz.app.ui.fragment.ordering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
}

/*
class FreeShippingSaysBS : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = BsFreeShippingDaysBinding.inflate(
        layoutInflater,
        container,
        false
    ).apply {
        this.incHeader.imgClose.setOnClickListener { dismiss() }
        this.incHeader.tvTitle.text = FreeShippingSaysBSArgs.fromBundle(requireArguments()).title
        this.tvContent.text = HtmlCompat.fromHtml(
            FreeShippingSaysBSArgs.fromBundle(requireArguments()).info,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }.root

}*/
