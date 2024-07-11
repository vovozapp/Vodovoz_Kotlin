package com.vodovoz.app.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class CustomRecycleView(ctx: Context, attrs: AttributeSet) : RecyclerView(ctx, attrs) {

    private var biggestHeight: Int = 0

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val h = child.measuredHeight
            if (h > biggestHeight) biggestHeight = h
        }

        setMeasuredDimension(measuredWidth, biggestHeight)
        super.onMeasure(widthSpec, heightSpec)
    }
}