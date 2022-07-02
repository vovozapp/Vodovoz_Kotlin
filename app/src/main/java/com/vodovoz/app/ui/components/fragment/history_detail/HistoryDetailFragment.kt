package com.vodovoz.app.ui.components.fragment.history_detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.FragmentHistoryDetailBinding
import com.vodovoz.app.ui.config.UIConfig
import com.vodovoz.app.ui.extensions.BannerActionExtensions.invoke
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.HistoryUI
import jp.shts.android.storiesprogressview.StoriesProgressView

class HistoryDetailFragment : Fragment() {

    companion object {
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

    private var currentBannerIndex = 0

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
                        currentBannerIndex = 0
                        iChangeHistory.nextHistory()
                    }
                }
            )

            image.setOnClickListener {
                binding.storiesProgress.skip()
            }

            close.setOnClickListener {
                parentFragment?.findNavController()?.popBackStack()
            }

            primaryButtonContainer.setOnClickListener {
                historyUI.bannerUIList[currentBannerIndex].actionEntity?.invoke(requireParentFragment().findNavController(), requireActivity())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        restartStories()
    }

    private fun restartStories() {
        binding.storiesProgress.startStories()
        updateBanner(historyUI.bannerUIList[currentBannerIndex])
        binding.storiesProgress.startStories(currentBannerIndex)
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
        when(bannerUI.actionEntity?.action) {
            null -> binding.primaryButtonContainer.visibility = View.GONE
            else -> {
                binding.primaryButton.text = bannerUI.actionEntity.action
                binding.primaryButtonContainer.setCardBackgroundColor(Color.parseColor(bannerUI.actionEntity.actionColor))
            }
        }

        Glide.with(requireContext())
            .load(bannerUI.detailPicture)
            .into(binding.image)
    }

    interface IChangeHistory {
        fun nextHistory()
    }

}