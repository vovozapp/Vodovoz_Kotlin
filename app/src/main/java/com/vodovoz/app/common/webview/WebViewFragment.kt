package com.vodovoz.app.common.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.databinding.FragmentWebViewFlowBinding

class WebViewFragment : BaseFragment() {

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

        initToolbar(args.title)

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

}
/*
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

}*/
