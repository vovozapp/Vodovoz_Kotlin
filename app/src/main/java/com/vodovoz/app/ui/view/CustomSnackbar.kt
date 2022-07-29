package com.vodovoz.app.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.LayoutCustomSnackbarBinding

class CustomSnackbar(
    parent: ViewGroup,
    content: CustomSnackbarView
) : BaseTransientBottomBar<CustomSnackbar>(parent, content, content) {

    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {

        fun make(viewGroup: ViewGroup): CustomSnackbar {
            val customView = LayoutCustomSnackbarBinding.inflate(
                LayoutInflater.from(viewGroup.context), viewGroup, false
            )

            return CustomSnackbar(viewGroup, customView.root)
        }

    }

}