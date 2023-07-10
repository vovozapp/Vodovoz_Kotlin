package com.vodovoz.app.feature.home.banneradvinfo

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsBannerAdvInfoBinding
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerAdvInfoBottomSheetFragment : BaseBottomSheetFragment() {

    companion object {
        fun newInstance(title: String, body: String, data: String): BannerAdvInfoBottomSheetFragment {
            return BannerAdvInfoBottomSheetFragment().apply {
                arguments = bundleOf("title" to title, "body" to body, "data" to data)
            }
        }
    }

    override fun layout(): Int = R.layout.bs_banner_adv_info

    private val binding: BsBannerAdvInfoBinding by viewBinding {
        BsBannerAdvInfoBinding.bind(contentView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            binding.titleAdvTv.text = requireArguments().getString("title")
            binding.bodyAdvTv.text = requireArguments().getString("body")
            binding.dataAdvTv.text = requireArguments().getString("data").fromHtml()
        }
    }
}