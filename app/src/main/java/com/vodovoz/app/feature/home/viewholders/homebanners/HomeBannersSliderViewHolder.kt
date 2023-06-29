package com.vodovoz.app.feature.home.viewholders.homebanners

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.*
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentSliderBannerBinding
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homebanners.inneradapter.HomeBannersInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homebanners.inneradapter.HomeBannersSliderClickListener
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.*
import java.util.*

class HomeBannersSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener,
    width: Int,
    ratio: Double,
    private val manager: BannerManager
) : ItemViewHolder<HomeBanners>(view) {

    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val binding: FragmentSliderBannerBinding = FragmentSliderBannerBinding.bind(view)
    private val homeBannersAdapter = HomeBannersInnerAdapter(getHomeBannersSliderClickListener())
    private var autoTimerTask: Timer? = Timer()

    init {
        val space = itemView.resources.getDimension(R.dimen.space_16).toInt()
        binding.vpBanners.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpBanners.adapter = homeBannersAdapter
        binding.vpBanners.apply {
            clipToPadding = false   // allow full width shown with padding
            clipChildren = false    // allow left/right item is not clipped
            offscreenPageLimit = 2  // make sure left/right item is rendered
        }

        binding.dotsIndicator.attachTo(binding.vpBanners)

// increase this offset to show more of left/right
        val offsetPx = space
        binding.vpBanners.setPadding(offsetPx, 0, offsetPx, 0)

// increase this offset to increase distance between 2 items
        val pageMarginPx = space/2
        val marginTransformer = MarginPageTransformer(pageMarginPx)
        binding.vpBanners.setPageTransformer(marginTransformer)

        binding.vpBanners.addItemDecoration(
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
                        //left = space
                        //right = space
                    }
                }
            }
        )

        binding.vpBanners.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            (width * ratio).toInt()
        )
    }

    override fun bind(item: HomeBanners) {
        super.bind(item)
        homeBannersAdapter.submitList(item.items)
        binding.vpBanners.enableAutoScroll(item.items.size)
    }

    private fun getHomeBannersSliderClickListener() : HomeBannersSliderClickListener {
        return object : HomeBannersSliderClickListener {
            override fun onBannerClick(entity: ActionEntity?) {
                clickListener.onBannerClick(entity)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun ViewPager2.enableAutoScroll(totalPages: Int): Timer? {
        if (autoTimerTask == null) autoTimerTask = Timer()
        var currentPageIndex = manager.fetchPosition()
        autoTimerTask?.schedule(object : TimerTask() {
            override fun run() {
                scope.launch {
                    currentItem = currentPageIndex++
                    manager.increase()
                    if (currentPageIndex == totalPages) {
                        currentPageIndex = 0
                    }
                }
            }
        }, 0, 4000)

        val callback = object: OnPageChangeCallback() {
            private var settled = false
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                manager.setPosition(position)
            }
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == SCROLL_STATE_DRAGGING) {
                    settled = false
                }
                if (state == SCROLL_STATE_SETTLING) {
                    settled = true
                }
                if (state == SCROLL_STATE_IDLE && !settled) {
                    if (manager.fetchPosition() == totalPages - 1) {
                        currentPageIndex = 0
                        currentItem = 0
                        manager.setPosition(0)
                    }
                }
            }
        }
        unregisterOnPageChangeCallback(callback)
        registerOnPageChangeCallback(callback)

        // Stop auto paging when user touch the view
        getRecyclerView().setOnTouchListener { _, event ->
            scope.cancel()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
            autoTimerTask?.cancel()
            autoTimerTask = null
            scope.launch {
                delay(10000)
                binding.vpBanners.enableAutoScroll(totalPages)
            }
            false
        }

        return autoTimerTask // Return the reference for cancel
    }

    fun ViewPager2.getRecyclerView(): RecyclerView {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        return recyclerViewField.get(this) as RecyclerView
    }

}
