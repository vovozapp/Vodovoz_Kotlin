package com.vodovoz.app.ui.view

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class Divider(
    private val divider: Drawable,
    private val addAfterLastItem: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        val dividerLeft: Int = parent.paddingLeft
        val dividerRight: Int = parent.width - parent.paddingRight
        val childCount: Int = parent.childCount
        val diff = when(addAfterLastItem) {
            true -> 0
            false -> 1
        }
        for (index in 0 until childCount - diff) {
            val child: View = parent.getChildAt(index)
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            val dividerTop: Int = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + divider.intrinsicHeight
            divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            divider.draw(canvas)
        }
    }

}