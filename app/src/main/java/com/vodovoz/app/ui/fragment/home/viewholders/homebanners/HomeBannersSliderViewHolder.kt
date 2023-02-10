package com.vodovoz.app.ui.fragment.home.viewholders.homebanners

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentSliderBannerBinding
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter.HomeBannersInnerAdapter
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter.HomeBannersSliderClickListener
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class HomeBannersSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener,
    width: Int,
    ratio: Double
) : RecyclerView.ViewHolder(view) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val binding: FragmentSliderBannerBinding = FragmentSliderBannerBinding.bind(view)
    private val homeBannersAdapter = HomeBannersInnerAdapter(getHomeBannersSliderClickListener())
    private var autoTimerTask: Timer? = null

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

        binding.vpBanners.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            (width * ratio).toInt()
        )
    }

    fun bind(items: HomeBanners) {
        homeBannersAdapter.submitList(items.items)
        if (autoTimerTask == null) {
            binding.vpBanners.enableAutoScroll(items.items.size)
        }
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
        autoTimerTask = Timer()
        var currentPageIndex = currentItem
        autoTimerTask?.schedule(object : TimerTask() {
            override fun run() {
                scope.launch {
                    currentItem = currentPageIndex++
                    if (currentPageIndex == totalPages) currentPageIndex = 0
                }
            }
        }, 0, 4000)

        // Stop auto paging when user touch the view
        getRecyclerView().setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) autoTimerTask?.cancel()
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
