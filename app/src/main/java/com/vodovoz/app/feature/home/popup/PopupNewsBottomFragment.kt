package com.vodovoz.app.feature.home.popup

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsPopupNewsBinding
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.util.extensions.color
import com.vodovoz.app.util.extensions.debugLog

class PopupNewsBottomFragment : BaseBottomSheetFragment() {

    companion object {
        fun newInstance(
            popupNewsUI: PopupNewsUI,
            clickListener: NewsClickListener
        ) = PopupNewsBottomFragment().apply {
            this.popupNewsUI = popupNewsUI
            this.clickListener = clickListener
        }
    }

    private lateinit var popupNewsUI: PopupNewsUI
    private lateinit var clickListener: NewsClickListener

    override fun layout(): Int = R.layout.bs_popup_news

    private val binding: BsPopupNewsBinding by viewBinding {
        BsPopupNewsBinding.bind(
            contentView
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        binding.incHeader.imgClose.isVisible = false
    }

    private fun initView() {
        binding.incHeader.tvTitle.text = popupNewsUI.name

        binding.tvDetails.text = popupNewsUI.detailText
        binding.tvDetails.isVisible = !popupNewsUI.detailText.isNullOrEmpty()

        binding.incHeader.imgClose.setOnClickListener { dismiss() }

        Glide.with(requireContext())
            .load(popupNewsUI.detailPicture)
            .into(binding.imgBanner)

        if (popupNewsUI.actionEntity?.action != null) {
            binding.btnShowDetails.text = popupNewsUI.actionEntity?.action
            binding.btnShowDetails.isVisible = true
            binding.btnShowDetails.setOnClickListener {
                popupNewsUI.actionEntity?.let { action ->
                    clickListener.onClick(action)
                }
                dismiss()
            }
        } else {
            binding.btnShowDetails.isVisible = false
        }

        if (popupNewsUI.actionEntity?.actionColor != null) {
            val color = Color.parseColor(popupNewsUI.actionEntity!!.actionColor)
            binding.btnShowDetails.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

}
/*
class PopupNewsBottomFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            popupNewsUI: PopupNewsUI,
            iOnInvokeAction: IOnInvokeAction
        ) = PopupNewsBottomFragment().apply {
            this.popupNewsUI = popupNewsUI
            this.iOnInvokeAction = iOnInvokeAction
        }
    }

    private lateinit var popupNewsUI: PopupNewsUI
    private lateinit var iOnInvokeAction: IOnInvokeAction

    private lateinit var binding: BsPopupNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BsPopupNewsBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initView()
        initDialog()
    }.root

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initView() {
        binding.incHeader.tvTitle.text = popupNewsUI.name
        binding.tvDetails.text = popupNewsUI.detailText
        binding.btnShowDetails.text = popupNewsUI.actionEntity?.action
        binding.incHeader.imgClose.setOnClickListener { dismiss() }
        Glide.with(requireContext())
            .load(popupNewsUI.detailPicture)
            .into(binding.imgBanner)

        binding.btnShowDetails.setOnClickListener {
            popupNewsUI.actionEntity?.let { action ->
                iOnInvokeAction.invokeAction(action)
            }
            dismiss()
        }
    }


    fun interface IOnInvokeAction {
        fun invokeAction(actionEntity: ActionEntity)
    }
}*/
