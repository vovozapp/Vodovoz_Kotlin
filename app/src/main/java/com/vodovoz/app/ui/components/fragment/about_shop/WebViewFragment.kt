package com.vodovoz.app.ui.components.fragment.about_shop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.databinding.FragmentWebViewBinding
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation

class WebViewFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentWebViewBinding
    private lateinit var url: String
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        WebViewFragmentArgs.fromBundle(requireArguments()).let { args ->
            url = args.url
            title = args.title
        }
    }

    override fun update() {
        binding.webContent.reload()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentWebViewBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initAppBar()
        initWebView()
    }

    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        onStateSuccess()
        binding.contentScrollContainer.setScrollElevation(binding.appbar)
        binding.webContent.settings.javaScriptEnabled = true
        binding.webContent.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?,
            ) {
                super.onReceivedError(view, request, error)
                onStateError("Ошибка")
            }
        }

        binding.webContent.loadUrl(url)
    }

}