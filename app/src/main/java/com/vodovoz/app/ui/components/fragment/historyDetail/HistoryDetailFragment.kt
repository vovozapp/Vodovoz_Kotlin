package com.vodovoz.app.ui.components.fragment.historyDetail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.FragmentHistoryDetailBinding
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.HistoryUI
import jp.shts.android.storiesprogressview.StoriesProgressView

class HistoryDetailFragment : Fragment() {

    companion object {
        private const val STORY_DURATION = 2000L
        fun newInstance(
            historyUI: HistoryUI,
            iChangeHistory: IChangeHistory
        ) = HistoryDetailFragment().apply {
            this.historyUI = historyUI
            this.iChangeHistory = iChangeHistory
        }
    }

    private lateinit var historyUI: HistoryUI
    private lateinit var iChangeHistory: IChangeHistory

    private lateinit var binding: FragmentHistoryDetailBinding
    private var currentBannerIndex = -1

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
    }.root

    private fun initView() {
        with(binding) {
            storiesProgress.setStoriesCount(historyUI.bannerUIList.size)
            storiesProgress.setStoryDuration(STORY_DURATION)
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
                        iChangeHistory.nextHistory()
                    }
                }
            )
            storiesProgress.startStories()

            close.setOnClickListener {
                parentFragment?.findNavController()?.popBackStack()
            }

            primaryButtonContainer.setOnClickListener {

            }
        }
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
        bannerUI.bannerActionEntity?.action?.let { action ->
            binding.primaryButton.text = action
        }
        bannerUI.bannerActionEntity?.actionColor?.let { color ->
            binding.primaryButtonContainer.setCardBackgroundColor(Color.parseColor(color))
        }
        Glide.with(requireContext())
            .load(bannerUI.detailPicture)
            .into(binding.image)
    }

    interface IChangeHistory {
        fun nextHistory()
    }

}