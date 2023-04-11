package com.vodovoz.app.ui.fragment.popup_news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.BsPopupNewsBinding
import com.vodovoz.app.feature.home.popup.NewsClickListener
import com.vodovoz.app.ui.model.PopupNewsUI

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
                clickListener.onClick(action)
            }
            dismiss()
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
