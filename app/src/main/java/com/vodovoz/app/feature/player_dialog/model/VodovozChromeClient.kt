package com.vodovoz.app.feature.player_dialog.model

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout

 class VodovozChromeClient(
     private val getDecorView: () -> View?,
     private val getActivity: () -> Activity,
     private val getResources: () -> Resources,
) : WebChromeClient() {
    private var mCustomView: View? = null
    private var mCustomViewCallback: CustomViewCallback? = null
    protected var mFullscreenContainer: FrameLayout? = null
    private var mOriginalOrientation = 0
    private var mOriginalSystemUiVisibility = 0

    override fun getDefaultVideoPoster(): Bitmap? {
        return if (super.getDefaultVideoPoster() == null) {
            try {
                BitmapFactory.decodeResource(
                    getResources(),
                    com.gapps.library.R.drawable.ic_vna_play_icon
                )
            } catch (e: Exception) {
                null
            }
        } else {
            super.getDefaultVideoPoster()
        }
    }

    override fun onHideCustomView() {
        (getDecorView() as FrameLayout).removeView(this.mCustomView)
        this.mCustomView = null
        getDecorView()?.systemUiVisibility = this.mOriginalSystemUiVisibility

        getActivity().requestedOrientation = this.mOriginalOrientation
        mCustomViewCallback!!.onCustomViewHidden()
        this.mCustomViewCallback = null
    }

    override fun onShowCustomView(
        paramView: View,
        paramCustomViewCallback: CustomViewCallback,
    ) {
        if (this.mCustomView != null) {
            onHideCustomView()
            return
        }
        this.mCustomView = paramView
        this.mOriginalSystemUiVisibility = getDecorView()?.systemUiVisibility ?: return
        this.mOriginalOrientation = getActivity().requestedOrientation
        this.mCustomViewCallback = paramCustomViewCallback
        (getDecorView() as FrameLayout).addView(
            mCustomView,
            FrameLayout.LayoutParams(-1, -1)
        )
        getActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE


        getDecorView()?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }
}
