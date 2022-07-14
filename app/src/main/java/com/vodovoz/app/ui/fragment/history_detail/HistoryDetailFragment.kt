package com.vodovoz.app.ui.fragment.history_detail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.FragmentHistoryDetailBinding
import com.vodovoz.app.ui.interfaces.IOnInvokeAction
import com.vodovoz.app.ui.interfaces.IOnChangeHistory
import com.vodovoz.app.ui.config.UIConfig
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.HistoryUI
import com.vodovoz.app.util.LogSettings
import jp.shts.android.storiesprogressview.StoriesProgressView

class HistoryDetailFragment : Fragment() {

    companion object {
        private const val HISTORY = "HISTORY"
        fun newInstance(
            historyUI: HistoryUI
        ) = HistoryDetailFragment().apply {
            arguments = bundleOf(Pair(HISTORY, historyUI))
        }
    }

    private lateinit var historyUI: HistoryUI
    private lateinit var iOnChangeHistory: IOnChangeHistory
    private lateinit var iOnInvokeAction: IOnInvokeAction

    private lateinit var binding: FragmentHistoryDetailBinding
    private var currentBannerIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        historyUI = requireArguments().getParcelable(HISTORY)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHistoryDetailBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initCallbacks()
        initView()
    }.root

    private fun initView() {
        with(binding) {
            storiesProgress.setStoriesCount(historyUI.bannerUIList.size)
            storiesProgress.setStoryDuration(UIConfig.STORY_DURATION)
            storiesProgress.setStoriesListener(
                object : StoriesProgressView.StoriesListener {
                    override fun onNext() {
                        increaseBannerIndex()
                        updateBanner(historyUI.bannerUIList[currentBannerIndex])
                    }

                    override fun onPrev() {
                        reduceBannerIndex()
                        updateBanner(historyUI.bannerUIList[currentBannerIndex])
                    }

                    override fun onComplete() {
                        iOnChangeHistory.nextHistory()
                    }
                }
            )

            close.setOnClickListener {
                iOnChangeHistory.close()
            }

            primaryButtonContainer.setOnClickListener {
                historyUI.bannerUIList[currentBannerIndex].actionEntity?.let { actionEntity ->
                    iOnInvokeAction.onInvokeAction(actionEntity)
                }
            }

            binding.previousHistory.setOnClickListener {
                binding.storiesProgress.reverse()
            }

            binding.nextHistory.setOnClickListener {
                if (currentBannerIndex == historyUI.bannerUIList.indices.last) {
                    iOnChangeHistory.nextHistory()
                } else {
                    binding.storiesProgress.skip()
                }
            }
        }
    }

    private fun initCallbacks() {
        iOnChangeHistory = requireParentFragment() as IOnChangeHistory
        iOnInvokeAction = requireParentFragment() as IOnInvokeAction
    }

    private fun reduceBannerIndex() {
        currentBannerIndex--
        if (currentBannerIndex < 0) currentBannerIndex = 0
    }

    private fun increaseBannerIndex() {
        currentBannerIndex++
        currentBannerIndex %= historyUI.bannerUIList.size
    }

    private fun updateBanner(bannerUI: BannerUI) {
        bannerUI.actionEntity?.action?.let { action ->
            binding.primaryButton.text = action
        }
        bannerUI.actionEntity?.actionColor?.let { color ->
            binding.primaryButtonContainer.setCardBackgroundColor(Color.parseColor(color))
        }
        Glide.with(requireContext())
            .load(bannerUI.detailPicture)
            .into(binding.image)
    }

    private fun startStories() {
        currentBannerIndex = 0
        updateBanner(historyUI.bannerUIList[currentBannerIndex])
        binding.storiesProgress.startStories()
    }

    override fun onResume() {
        super.onResume()
        startStories()
    }

    override fun onPause() {
        super.onPause()
        binding.image.setImageDrawable(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.storiesProgress.destroy()
    }
}