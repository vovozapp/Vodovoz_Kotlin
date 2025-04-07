package com.vodovoz.app.feature.player_dialog

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.gapps.library.api.VideoService.Companion.build
import com.gapps.library.api.models.video.VideoPreviewModel
import com.vodovoz.app.R
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.databinding.DialogFragmentRutubeVideoBinding
import com.vodovoz.app.feature.player_dialog.model.VodovozChromeClient
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class RuTubeVideoFragmentDialog : DialogFragment() {

    private lateinit var binding: DialogFragmentRutubeVideoBinding
    private lateinit var videoCode: String
    private lateinit var videoInfo: VideoPreviewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        getArgs()
    }

    private fun getArgs() {
        videoCode = YouTubeVideoFragmentDialogArgs.fromBundle(requireArguments()).videoId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = DialogFragmentRutubeVideoBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initView()
        initVideoService()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.statusBarColor = Color.WHITE
    }


    private fun initVideoService() {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val videoService = build {
            with(requireContext())
            httpClient(okHttpClient)
            enableCache(true)
            enableLog(true)
        }

        videoService.loadVideoPreview(
            url = ApiConfig.RUTUBE_URL + videoCode,
            onSuccess = { model ->
                videoInfo = model
                val linkToPlay = model.linkToPlay
                with(binding.rutubePlayerView) {
                    updateWebViewSize(false)
                    if (linkToPlay != null) {
                        loadUrl(linkToPlay)
                    }
                }
            }
        )
    }

    private fun updateWebViewSize(landscapeOrientation: Boolean) {
        val windowWidth = resources.displayMetrics.widthPixels
        val windowHeight = resources.displayMetrics.heightPixels
        val rutubePlayerView = binding.rutubePlayerView
        rutubePlayerView.layoutParams = rutubePlayerView.layoutParams.apply {
            width = windowWidth
            height = if (landscapeOrientation) {
                windowHeight
            } else {
                windowWidth * videoInfo.height / videoInfo.width
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateWebViewSize(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        binding.close.setOnClickListener {
            dismiss()
        }
        binding.rutubePlayerView.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blackTextDark))

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?,
                ): Boolean = true
            }

            webChromeClient = VodovozChromeClient(
                getDecorView = { dialog?.window?.decorView },
                getResources = { requireContext().resources },
                getActivity = { requireActivity() }
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
                mediaPlaybackRequiresUserGesture = false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.rutubePlayerView.saveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding.rutubePlayerView.restoreState(savedInstanceState ?: return)
    }

}