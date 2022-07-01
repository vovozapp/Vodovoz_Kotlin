package com.vodovoz.app.ui.components.fragment.slider.banner_slider

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.data.model.common.BannerActionEntity
import com.vodovoz.app.databinding.FragmentSliderBannerBinding
import com.vodovoz.app.ui.components.adapter.BannersSliderAdapter
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.BannerDiffUtilCallback
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.Serializable


class BannerSliderFragment : BaseHiddenFragment() {

    companion object {
        fun newInstance(
            bannerSliderConfig: BannerSliderConfig,
            onBannerClickSubject: PublishSubject<BannerActionEntity>,
            viewStateSubject: PublishSubject<ViewState>? = null,
            onUpdateSubject: PublishSubject<Boolean>? = null
        ) = BannerSliderFragment().apply {
            this.viewStateSubject = viewStateSubject
            this.bannerSliderConfig = bannerSliderConfig
            this.onUpdateSubject = onUpdateSubject
            this.onBannerClickSubject = onBannerClickSubject
        }
    }

    private val compositeDisposable = CompositeDisposable()

    private var viewStateSubject: PublishSubject<ViewState>? = null
    private var onUpdateSubject: PublishSubject<Boolean>? = null
    private lateinit var bannerSliderConfig: BannerSliderConfig
    private lateinit var onBannerClickSubject: PublishSubject<BannerActionEntity>

    private lateinit var binding: FragmentSliderBannerBinding
    private lateinit var viewModel: BannerSliderViewModel

    private lateinit var bannersSliderAdapter: BannersSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[BannerSliderViewModel::class.java]
        viewModel.updateArgs(bannerSliderConfig.bannerType)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderBannerBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initBannerRecyclerView()
    }

    private fun initBannerRecyclerView() {
        binding.bannerPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.bannerPager.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.bannerPager.width != 0) {
                        binding.bannerPager.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        binding.bannerPager.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (binding.bannerPager.width * bannerSliderConfig.ratio).toInt()
                        )
                        bannersSliderAdapter = BannersSliderAdapter(onBannerClickSubject)
                        binding.bannerPager.adapter = bannersSliderAdapter
                        observeViewModel()
                    }
                }
            }
        )

        binding.bannerPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.restartCountDownTimer(position)
                }
            }
        )

        binding.bannerPager.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        top = bannerSliderConfig.marginTop
                        bottom = bannerSliderConfig.marginBottom
                        left = bannerSliderConfig.marginLeft
                        right = bannerSliderConfig.marginRight
                    }
                }
            }
        )
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> {
                    viewStateSubject?.onNext(state)
                    hide()
                }
                else -> viewStateSubject?.onNext(state)
            }
        }

        viewModel.bannerUIListLD.observe(viewLifecycleOwner) { bannerUIList ->
            val diffUtil = BannerDiffUtilCallback(
                oldList = bannersSliderAdapter.bannerUIList,
                newList = bannerUIList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                bannersSliderAdapter.bannerUIList = bannerUIList
                diffResult.dispatchUpdatesTo(bannersSliderAdapter)
            }
        }

        viewModel.currentBannerIndexLD.observe(viewLifecycleOwner) { index ->
            binding.bannerPager.setCurrentItem(index, true)
        }
    }

    override fun onStart() {
        super.onStart()

        onUpdateSubject?.subscribeBy {
            viewModel.updateData()
        }?.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    class BannerSliderConfig(
        val bannerType: String,
        val marginTop: Int,
        val marginBottom: Int,
        val marginLeft: Int,
        val marginRight: Int,
        val ratio: Double
    ) : Serializable

}