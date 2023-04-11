package com.vodovoz.app.common.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.databinding.FragmentWebViewFlowBinding

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
            binding.wvContent.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }
            binding.wvContent.loadUrl(args.url)
        } catch (e: Throwable) {
            showError(e.toErrorState())
        }
    }
}