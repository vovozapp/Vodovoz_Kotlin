package com.vodovoz.app.feature.home.ratebottom

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.FragmentRateBottomBinding
import com.vodovoz.app.feature.home.ratebottom.adapter.RateBottomClickListener
import com.vodovoz.app.feature.home.ratebottom.adapter.RateBottomImageAdapter
import com.vodovoz.app.feature.home.ratebottom.adapter.RateBottomViewPagerAdapter
import com.vodovoz.app.feature.productdetail.sendcomment.SendCommentAboutProductBottomDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RateBottomFragment : BaseBottomSheetFragment() {

    override fun layout(): Int {
        return R.layout.fragment_rate_bottom
    }

    private val binding: FragmentRateBottomBinding by viewBinding {
        FragmentRateBottomBinding.bind(contentView)
    }

    private val viewModel: RateBottomViewModel by activityViewModels()

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val collapsedImagesAdapter = RateBottomImageAdapter()
    private val rateBottomViewPagerAdapter = RateBottomViewPagerAdapter(object : RateBottomClickListener {

        override fun dontCommentProduct(id: Long) {
            ratingProductManager.dontCommentProduct(id)
            dialog?.dismiss()
        }

        override fun rateProduct(id: Long, ratingCount: Int) {
            SendCommentAboutProductBottomDialog.newInstance(id, ratingCount).show(childFragmentManager, "TAG")
        }

    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindErrorRefresh { viewModel.refresh() }
        observeUiState()
        initImageRv()
        initViewPager()
        initBottomSheetCallback()
    }

    private fun initViewPager() {
        binding.rateViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.rateViewPager.adapter = rateBottomViewPagerAdapter
        binding.dotsIndicator.attachTo(binding.rateViewPager)
    }

    private fun initBottomSheetCallback() {
        val behavior = (dialog as? BottomSheetDialog)?.behavior
        val density = requireContext().resources.displayMetrics.density
        behavior?.peekHeight = (160 * density).toInt()
        behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {
                    binding.collapsedLL.alpha = 1 - 2 * slideOffset
                    binding.expandedLL.alpha = slideOffset * slideOffset

                    if (slideOffset > 0.5) {
                        binding.collapsedLL.visibility = View.GONE
                        binding.expandedLL.visibility = View.VISIBLE
                        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    }

                    if (slideOffset < 0.5 && binding.expandedLL.visibility == View.VISIBLE) {
                        binding.collapsedLL.visibility = View.VISIBLE
                        binding.expandedLL.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    private fun initImageRv() {
        with(binding.collapsedRv) {
            adapter = collapsedImagesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeUiState()
                .collect { state ->
                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    if (state.data.item != null) {
                        binding.expandedHeaderTv.text = state.data.item.rateBottomData?.titleProduct
                        val prList = state.data.item.rateBottomData?.productsList
                        if (!prList.isNullOrEmpty()) {
                            rateBottomViewPagerAdapter.submitList(prList)
                        }
                    }

                    if (state.data.collapsedData != null) {
                        binding.collapsedBodyTv.text = state.data.collapsedData.body
                        binding.collapsedHeaderTv.text = state.data.collapsedData.title
                        if (!state.data.collapsedData.imageList.isNullOrEmpty()) {
                            collapsedImagesAdapter.submitList(state.data.collapsedData.imageList)
                        }
                    }

                    showError(state.error)
                }
        }
    }
}