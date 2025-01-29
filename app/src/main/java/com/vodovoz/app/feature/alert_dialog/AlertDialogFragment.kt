package com.vodovoz.app.feature.alert_dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.vodovoz.app.databinding.FragmentAlertDialogBinding


class AlertDialogFragment : DialogFragment() {

    companion object {
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_DESCRIPTION = "KEY_DESCRIPTION"
        private const val KEY_POSITIVE = "KEY_POSITIVE"
        private const val KEY_NEGATIVE = "KEY_NEGATIVE"


        fun newInstance(
            title: String,
            description: String,
            positiveText: String,
            negativeText: String,
            onPositiveClick: () -> Unit,
            onNegativeClick: () -> Unit = {},
        ): AlertDialogFragment {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putString(KEY_DESCRIPTION, description)
            args.putString(KEY_NEGATIVE, negativeText)
            args.putString(KEY_POSITIVE, positiveText)

            val fragment = AlertDialogFragment().apply {
                setPositiveClick { onPositiveClick() }
                setNegativeClick { onNegativeClick() }
            }
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var viewBinding: FragmentAlertDialogBinding

    private var onPositiveClick: () -> Unit = {}
    private var onNegativeClick: () -> Unit = {}

    fun setPositiveClick(block: () -> Unit) {
        onPositiveClick = block
    }

    fun setNegativeClick(block: () -> Unit) {
        onNegativeClick = block
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding = FragmentAlertDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getString(KEY_TITLE) ?: ""
        val description = arguments?.getString(KEY_DESCRIPTION) ?: ""
        val positiveText = arguments?.getString(KEY_POSITIVE) ?: ""
        val negativeText = arguments?.getString(KEY_NEGATIVE) ?: ""

        viewBinding.tvTitle.text = title
        viewBinding.tvDescription.text = description
        viewBinding.btnNegative.text = negativeText
        viewBinding.btnPositive.text = positiveText

        viewBinding.btnNegative.setOnClickListener {
            onNegativeClick()
            dismiss()
        }

        viewBinding.btnPositive.setOnClickListener {
            onPositiveClick()
        }
    }


}