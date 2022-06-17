package com.vodovoz.app.ui.components.fragment.bannerSlider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.databinding.FragmentSliderBannerBinding
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.bannerSliderAdapter.BannerSliderAdapter
import com.vodovoz.app.ui.components.diffUtils.BannerDiffUtilCallback
import com.vodovoz.app.ui.components.adapter.bannerSliderAdapter.BannerSliderMarginDecoration
import io.reactivex.rxjava3.subjects.PublishSubject


class BannerSliderFragment() : Fragment() {

    companion object {

        private const val CONFIG = "CONFIG"

        fun newInstance(
            bannerSliderConfig: BannerSliderConfig,
            readyViewSubject: PublishSubject<Boolean>
        ) = BannerSliderFragment().apply {
            this.readyViewSubject = readyViewSubject
            arguments = Bundle().also {
                it.putSerializable(CONFIG, bannerSliderConfig)
            }
        }
    }

    private lateinit var readyViewSubject: PublishSubject<Boolean>

    private lateinit var binding: FragmentSliderBannerBinding
    private lateinit var viewModel: BannerSliderViewModel

    private lateinit var bannerSliderAdapter: BannerSliderAdapter
    private lateinit var bannerSliderConfig: BannerSliderConfig

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentSliderBannerBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initViewModel()
        getArgs()
        initMainBannerRecyclerView()
    }.root

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[BannerSliderViewModel::class.java]
    }

    private fun getArgs() {
        arguments?.let { args ->
            args.getSerializable(CONFIG)?.let { config ->
                bannerSliderConfig = config as BannerSliderConfig
                viewModel.setBannerType(bannerSliderConfig.bannerType)
            }
        }
    }

    private fun initMainBannerRecyclerView() {
        binding.mainBannersViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.mainBannersViewPager.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.mainBannersViewPager.width != 0) {
                        binding.mainBannersViewPager.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        binding.mainBannersViewPager.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (binding.mainBannersViewPager.width * bannerSliderConfig.ratio).toInt()
                        )
                        bannerSliderAdapter = BannerSliderAdapter()
                        binding.mainBannersViewPager.adapter = bannerSliderAdapter
                        observeViewModel()
                    }
                }
            }
        )

        binding.mainBannersViewPager.addItemDecoration(BannerSliderMarginDecoration(
            marginTop = bannerSliderConfig.marginTop,
            marginBottom = bannerSliderConfig.marginBottom,
            marginLeft = bannerSliderConfig.marginLeft,
            marginRight = bannerSliderConfig.marginRight
        ))
    }

    private fun observeViewModel() {
        viewModel.mainBannerListLD.observe(viewLifecycleOwner) { mainBannerUIDataList ->
            val diffUtil = BannerDiffUtilCallback(
                oldList = bannerSliderAdapter.bannerUIList,
                newList = mainBannerUIDataList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                bannerSliderAdapter.bannerUIList = mainBannerUIDataList
                diffResult.dispatchUpdatesTo(bannerSliderAdapter)
            }

            readyViewSubject.onNext(true)
        }

        viewModel.sateHideLD.observe(viewLifecycleOwner) { stateHide ->
            when(stateHide) {
                true -> binding.root.visibility = View.VISIBLE
                false -> binding.root.visibility = View.GONE
            }
        }

        viewModel.currentBannerIndexLD.observe(viewLifecycleOwner) { index ->
            binding.mainBannersViewPager.setCurrentItem(index, true)
        }
    }

}