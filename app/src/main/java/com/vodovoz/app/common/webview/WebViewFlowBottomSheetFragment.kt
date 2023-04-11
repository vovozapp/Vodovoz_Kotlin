package com.vodovoz.app.common.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.databinding.FragmentWebViewFlowBinding
import com.vodovoz.app.ui.fragment.about_shop.WebViewFragmentArgs

class WebViewFlowBottomSheetFragment : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.fragment_web_view_flow

    private val binding: FragmentWebViewFlowBinding by viewBinding {
        FragmentWebViewFlowBinding.bind(
            contentView
        )
    }

    private val args: WebViewFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindErrorRefresh {
            binding.wvContent.reload()
        }

        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.wvContent.settings.javaScriptEnabled = true

        try {
            binding.wvContent.loadUrl(args.url)
        } catch (e: Throwable) {
            showError(e.toErrorState())
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.dismiss()
    }
}