package com.vodovoz.app.ui.fragment.slider.banners_slider

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentSliderBannerBinding
import com.vodovoz.app.ui.adapter.BannersSliderAdapter
import com.vodovoz.app.ui.base.BaseHiddenFragment
import com.vodovoz.app.ui.diffUtils.BannerDiffUtilCallback
import com.vodovoz.app.ui.interfaces.IOnInvokeAction
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.model.BannerUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class BannersSliderFragment : BaseHiddenFragment() {

    companion object {
        private const val BANNER_RATIO = "BANNER_RATIO"
        fun newInstance(
            bannerRatio: Double
        ) = BannersSliderFragment().apply {
            arguments = bundleOf(Pair(BANNER_RATIO, bannerRatio))
        }
    }

    private lateinit var bannerUIList: List<BannerUI>
    private var bannerRatio: Double = 0.0
    private lateinit var iOnInvokeAction: IOnInvokeAction

    private lateinit var binding: FragmentSliderBannerBinding

    private val compositeDisposable = CompositeDisposable()
    private val onBannerClickSubject: PublishSubject<ActionEntity> = PublishSubject.create()
    private var bannersSliderAdapter = BannersSliderAdapter(onBannerClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        subscribeSubjects()
    }

    private fun getArgs() {
        bannerRatio = requireArguments().getDouble(BANNER_RATIO)
    }

    private fun subscribeSubjects() {
        onBannerClickSubject.subscribeBy{ action ->
            iOnInvokeAction.onInvokeAction(action)
        }.addTo(compositeDisposable)
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
        binding.bannerPager.adapter = bannersSliderAdapter
        binding.bannerPager.onRenderFinished { width, _ ->
            binding.bannerPager.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (width * bannerRatio).toInt()
            )
        }


        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.bannerPager.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        top = space/2
                        bottom = space/2
                        left = space
                        right = space
                    }
                }
            }
        )
    }

    fun initCallbacks(iOnInvokeAction: IOnInvokeAction) {
        this.iOnInvokeAction = iOnInvokeAction
    }

    fun updateData(bannerUIList: List<BannerUI>) {
        this.bannerUIList = bannerUIList

        val diffUtil = BannerDiffUtilCallback(
            oldList = bannersSliderAdapter.bannerUIList,
            newList = bannerUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            bannersSliderAdapter.bannerUIList = bannerUIList
            diffResult.dispatchUpdatesTo(bannersSliderAdapter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}