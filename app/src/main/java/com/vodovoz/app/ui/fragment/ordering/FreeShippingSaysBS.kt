package com.vodovoz.app.ui.fragment.ordering

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.databinding.BsFreeShippingDaysBinding

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

}