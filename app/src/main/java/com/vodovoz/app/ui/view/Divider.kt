package com.vodovoz.app.ui.view

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView


class Divider(
    private val divider: Drawable,
    private val marginTop: Int,
    private val addAfterLastItem: Boolean = false,
    private val excludeIndexList: List<Int> = listOf()
) : RecyclerView.ItemDecoration() {
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0..childCount - 2) {
            if (excludeIndexList.contains(i)) continue
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerTop = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + divider.intrinsicHeight
            divider.setBounds(dividerLeft, dividerTop + marginTop, dividerRight, dividerBottom + marginTop)
            divider.draw(canvas)
        }
    }
}