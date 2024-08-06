package com.vodovoz.app.feature.player_dialog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.gapps.library.api.VideoService.Companion.build
import com.vodovoz.app.R
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.databinding.DialogFragmentRutubeVideoBinding
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class RuTubeVideoFragmentDialog : DialogFragment() {

    private lateinit var binding: DialogFragmentRutubeVideoBinding
    private lateinit var videoCode: String

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
            ApiConfig.RUTUBE_URL + videoCode,
            onSuccess = { model ->
                val linkToPlay = model.linkToPlay
                with(binding.rutubePlayerView) {
                    layoutParams.apply {
                        if (model.width != 0) {
                            val windowWidth = resources.displayMetrics.widthPixels
                            this.width = windowWidth
                            this.height = this.width * model.height / model.width
                        }
                    }
                    if (linkToPlay != null) {
                        loadUrl(linkToPlay)
                    }
                }
            })
    }

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
                ): Boolean {
                    return true
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun getDefaultVideoPoster(): Bitmap? {
                    return if (super.getDefaultVideoPoster() == null) {
                        try {
                            BitmapFactory.decodeResource(
                                context.resources,
                                com.gapps.library.R.drawable.ic_vna_play_icon
                            )
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        super.getDefaultVideoPoster()
                    }
                }
            }

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
            }
        }
    }

}