package com.vodovoz.app.ui.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView


class ExtendedBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    override fun getMaxItemCount(): Int {
        return 6
    }
}