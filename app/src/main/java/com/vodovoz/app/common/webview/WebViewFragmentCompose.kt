package com.vodovoz.app.common.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.common.webview.model.WebViewEvents
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.effects.LifecycleEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class WebViewFragment : Fragment() {


    @Inject
    lateinit var tabManager: TabManager

    private val viewModel by viewModels<WebViewViewModel>()

    override fun onStart() {
        super.onStart()
        tabManager.changeTabVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        tabManager.changeTabVisibility(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                VodovozTheme {
                    val viewState by viewModel.state.collectAsStateWithLifecycle()

                    WebViewScreen(
                        viewModel = viewModel,
                        viewState = viewState
                    )

                    LifecycleEffect {
                        viewModel.events.collect{ event ->
                            when(event){
                                WebViewEvents.GoBack -> findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }


//    @SuppressLint("SetJavaScriptEnabled")
//    private fun initWebView() {
//        binding.wvContent.settings.javaScriptEnabled = true
//        if (args.url.contains("#")) {
//            binding.wvContent.loadDataWithBaseURL(args.url.substringBefore("#"), "", "text/html", "utf-8", null)
//        }
//        binding.wvContent.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView?, url: String?) {
//                super.onPageFinished(view, url)
//                if (url != null && !url.contains("#") && args.url.contains("#")) {
//                    binding.wvContent.loadUrl(args.url)
//                }
//            }
//        }
//
//        try {
//            if (!args.url.contains("#")) {
//                binding.wvContent.loadUrl(args.url)
//            }
//        } catch (e: Throwable) {
//            showError(e.toErrorState())
//        }
//    }

}
