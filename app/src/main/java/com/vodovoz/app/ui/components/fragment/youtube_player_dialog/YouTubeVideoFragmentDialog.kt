package com.vodovoz.app.ui.components.fragment.youtube_player_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.youtube.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.vodovoz.app.R
import com.vodovoz.app.databinding.DialogFragmentVideoBinding


class YouTubeVideoFragmentDialog : DialogFragment() {

    private lateinit var binding: DialogFragmentVideoBinding
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
    ) = DialogFragmentVideoBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initView()
    }.root

    private fun initView() {
        lifecycle.addObserver(binding.youtubePlayerView)
        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoCode, 0)
            }
        })
    }

}