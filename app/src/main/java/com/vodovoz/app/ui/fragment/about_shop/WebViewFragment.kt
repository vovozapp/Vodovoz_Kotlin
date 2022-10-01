package com.vodovoz.app.ui.fragment.about_shop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.databinding.FragmentWebViewBinding
import com.vodovoz.app.ui.base.ViewStateBaseFragment
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
        binding.wvContent.reload()
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
        binding.incAppBar.tvTitle.text = title
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        onStateSuccess()
        binding.svContentContainer.setScrollElevation(binding.appbar)
        binding.wvContent.settings.javaScriptEnabled = true
        binding.wvContent.loadUrl(url)
    }

}