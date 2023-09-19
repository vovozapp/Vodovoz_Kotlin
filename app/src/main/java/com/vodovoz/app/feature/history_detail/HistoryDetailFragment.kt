package com.vodovoz.app.feature.history_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.FragmentHistoryDetailBinding
import com.vodovoz.app.ui.config.UIConfig
import com.vodovoz.app.ui.interfaces.IOnChangeHistory
import com.vodovoz.app.ui.interfaces.IOnInvokeAction
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.HistoryUI
import com.vodovoz.app.util.extensions.getParcelableSafe
import jp.shts.android.storiesprogressview.StoriesProgressView

class HistoryDetailFragment : Fragment() {

    companion object {
        private const val HISTORY = "HISTORY"
        fun newInstance(
            historyUI: HistoryUI,
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
        historyUI = requireArguments().getParcelableSafe(HISTORY, HistoryUI::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
            spProgress.setStoriesCount(historyUI.bannerUIList.size)
            spProgress.setStoryDuration(UIConfig.STORY_DURATION)
            spProgress.setStoriesListener(
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

            imgClose.setOnClickListener {
                iOnChangeHistory.close()
            }

            btnAction.setOnClickListener {
                historyUI.bannerUIList[currentBannerIndex].actionEntity?.let { actionEntity ->
                    iOnInvokeAction.onInvokeAction(actionEntity)
                }
            }

            binding.vPreviousHistory.setOnClickListener {
                binding.spProgress.reverse()
            }

            binding.vNextHistory.setOnClickListener {
                if (currentBannerIndex == historyUI.bannerUIList.indices.last) {
                    iOnChangeHistory.nextHistory()
                } else {
                    binding.spProgress.skip()
                }
            }
        }
    }

    private fun initCallbacks() {
        iOnChangeHistory = requireParentFragment() as IOnChangeHistory
        iOnInvokeAction = requireParentFragment() as IOnInvokeAction
    }

    internal fun reduceBannerIndex() {
        currentBannerIndex--
        if (currentBannerIndex < 0) currentBannerIndex = 0
    }

    internal fun increaseBannerIndex() {
        currentBannerIndex++
        currentBannerIndex %= historyUI.bannerUIList.size
    }

    internal fun updateBanner(bannerUI: BannerUI) {
        val action = bannerUI.actionEntity?.action
        binding.btnAction.isVisible = action != null
        binding.btnAction.text = action

        Glide.with(requireContext())
            .load(bannerUI.detailPicture)
            .into(binding.imgHistory)
    }

    private fun startStories() {
        currentBannerIndex = 0
        updateBanner(historyUI.bannerUIList[currentBannerIndex])
        binding.spProgress.startStories()
    }

    override fun onResume() {
        super.onResume()
        startStories()
    }

    override fun onPause() {
        super.onPause()
        binding.imgHistory.setImageDrawable(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.spProgress.destroy()
    }
}